

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd._
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

object MoviesRecommendation {
  def main(args: Array[String]): Unit = {

    /**
      * 项目运行配置
      */
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    val conf = new SparkConf().setAppName("MoviesRecommendation").setMaster("local[*]")
    val sc = new SparkContext(conf)


    /**
      * 第一步：读取电影评分的数据
      */

    //裝載用戶評分
    val myRatings = loadRatings("/input/ml-1m/personalRatings.txt")
    val myRatingsRDD = sc.parallelize(myRatings, 1)

    //读取电影信息到本地
    val movies = sc.textFile("/input/ml-1m/movies.dat").map { line =>
      val fields = line.split("::")
      (fields(0).toInt, fields(1))
    }.collect().toMap

    //读取评分数据
    val ratings = sc.textFile("/input/ml-1m/ratings.dat").map { line =>
      val fields = line.split("::")
      val rating = Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
      val timestamp = fields(3).toLong % 10
      (timestamp, rating)
    }

    //输出数据集基本信息
    val numRatings = ratings.count
    val numUsers = ratings.map(_._2.user).distinct.count
    val numMovies = ratings.map(_._2.product).distinct.count

    println("总共获得" + numRatings + "条评分，来自于" + numUsers + "个用户，共" + numMovies + "部电影。")


    /**
      * 第二步：利用timestamp将数据集分为训练集（timestamp<6)、验证集（6<timestamp<8)和测试集（timestamp>8)
      */
    val training = ratings.filter(x => x._1 < 6).values.union(myRatingsRDD).repartition(4).cache()
    val validation = ratings.filter(x => x._1 >= 6 && x._1 < 8).values.repartition(4).cache()
    val test = ratings.filter(x => x._1 >= 8).values.repartition(4).cache()

    val numTraining = training.count
    val numValidation = validation.count
    val numTest = test.count

    println("训练： " + numTraining + " 验证： " + numValidation + " 测试： " + numTest)


    /**
      * 第四步：使用不同的参数训练协同过滤模型，并且选择出RMSE最小的模型（为了简单起见，只从一个最小的参数范围选择：矩阵分解的秩从8-12中选择，
      * 正则系数从1.0～10.0 中选择，迭代次数从10～20 中选择，共计8个模型。
      */
    val ranks = List(8, 12)
    val lambdas = List(0.1, 10.0)
    val numIters = List(10, 20)
    var bestModel: Option[MatrixFactorizationModel] = None
    var bestValidationRmse = Double.MaxValue
    var bestRank = 0
    var bestLambda = -1.0
    var bestNumIter = -1
    for (rank <- ranks; lambda <- lambdas; numIter <- numIters) {
      val model = ALS.train(training, rank, numIter, lambda)
      val validationRmse = computeRmse(model, validation)

      if (validationRmse < bestValidationRmse) {
        bestModel = Some(model)
        bestValidationRmse = validationRmse
        bestRank = rank
        bestLambda = lambda
        bestNumIter = numIter
      }
    }

    // 用最佳模型预测测试集的评分，并计算和实际评分之间的均方根误差
    val testRmse = computeRmse(bestModel.get, test)
    println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".")


    /**
      * 步骤五：对比使用协同过滤算法和不使用协同过滤算法
      */
    val meanRating = training.union(validation).map(_.rating).mean
    val baselineRmse = math.sqrt(test.map(x => (meanRating - x.rating) * (meanRating - x.rating)).mean)
    val improvement = (baselineRmse - testRmse) / baselineRmse * 100
    println("The best model improves the baseline by " + "%1.2f".format(improvement) + "%.")


    /**
      * 步骤六：推荐前十部最感兴趣的电影，注意要剔除用户已经评分的电影
      */


    val myRatedMovieIds = myRatings.map(_.product).toSet
    val candidates = sc.parallelize(movies.keys.filter(!myRatedMovieIds.contains(_)).toSeq)
    val recommendations = bestModel.get.predict(candidates.map((0, _))).collect.sortBy(-_.rating).take(10)
    var i = 1
    println("#############################################################")
    println("親，为您推荐的10部电影如下：")
    recommendations.foreach { r =>
      println("%2d".format(i) + ": " + movies(r.product))
      i += 1
    }
    println("#############################################################")

    //结束应用
    sc.stop()
  }


  /**
    * 第三步：定义函数计算均方误差RMSE：
    *
    * @param model
    * @param data
    * @return
    */
  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating]): Double = {
    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    val predictionsAndRatings = predictions.map { x => ((x.user, x.product), x.rating) }
      .join(data.map(x => ((x.user, x.product), x.rating))).values
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).mean())
  }


  /**
    * 加载用户评分数据函數
    *
    * @param path
    * @return
    */
  def loadRatings(path: String): Seq[Rating] = {
    val lines = Source.fromFile(path).getLines()
    val ratings = lines.map {
      line =>
        val fields = line.split("::")
        Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }.filter(_.rating > 0.0)
    if (ratings.isEmpty) {
      sys.error("沒有評分數據！")
    }
    else {
      ratings.toSeq
    }
  }
}