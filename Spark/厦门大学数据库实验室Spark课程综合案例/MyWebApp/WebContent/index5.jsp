<%@ page language="java" import="dbtaobao.connDb,java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<String[]> list = connDb.index_4();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECharts 可视化分析淘宝双11</title>
<link href="./css/style.css" type='text/css' rel="stylesheet" />
<script src="./js/echarts.min.js"></script>
</head>
<body>
	<div class='header'>
		<p>ECharts 可视化分析淘宝双11</p>
	</div>
	<div class="content">
		<div class="nav">
			<ul>
				<li><a href="./index.jsp">所有买家各消费行为对比</a></li>
				<li><a href="./index1.jsp">男女买家交易对比</a></li>
				<li><a href="./index2.jsp">男女买家各个年龄段交易对比</a></li>
				<li><a href="./index3.jsp">商品类别交易额对比</a></li>
				<li><a href="./index4.jsp">回头客预测分数对比</a></li>
				<li class="current"><a href="#">各省份的总成交量对比</a></li>
			</ul>
		</div>
		<div class="container">
			<div class="title">回头客预测分数对比</div>
			<div class="show">
				<div class='chart-type'>回头客</div>
				<div id="main"></div>
			</div>
		</div>
	</div>
	<script>
		
	</script>
</body>
</html>