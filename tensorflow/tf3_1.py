# coding:utf-8
import tensorflow as tf

# a = tf.constant([1.0,2.0,3.0])
# b = tf.constant([3.0,4.0,5.0])

# result = a + b

# print result

# x = tf.constant([[1.0,2.0]])
# w = tf.constant([[3.0],[4.0]])

# y = tf.matmul(x,w)

# print y

# with tf.Session() as sess:
# 	print sess.run(y)

# # 定义输入和输出参数
# # x = tf.constant([[0.7,0.5]])
# x = tf.placeholder(tf.float32,shape=(1,2))
# w1 = tf.Variable(tf.random_normal([2,3],stddev=1,seed=1))
# w2 = tf.Variable(tf.random_normal([3,1],stddev=1,seed=1))

# #定义前向传播过程
# a = tf.matmul(x, w1)
# y = tf.matmul(a, w2)

# #用会话计算结果
# with tf.Session() as sess:
# 	init_op = tf.global_variables_initializer()
# 	sess.run(init_op)
# 	#print("y in tf_3.py is:\n",sess.run(y))
# 	print("y in tf_3.py is:\n",sess.run(y,feed_dict={x:[[0.7,0.5]]}))


# 定义输入和输出参数
# x = tf.constant([[0.7,0.5]])
x = tf.placeholder(tf.float32,shape=(None,2))
w1 = tf.Variable(tf.random_normal([2,3],stddev=1,seed=1))
w2 = tf.Variable(tf.random_normal([3,1],stddev=1,seed=1))

#定义前向传播过程
a = tf.matmul(x, w1)
y = tf.matmul(a, w2)

#用会话计算结果
with tf.Session() as sess:
	init_op = tf.global_variables_initializer()
	sess.run(init_op)
	#print("y in tf_3.py is:\n",sess.run(y)
	print "y in tf_3.py is:\n",sess.run(y,feed_dict={x:[[0.7,0.5],[0.2,0.3],[0.3,0.4],[0.4,0.5]]})
	print "w1:\n",sess.run(w1)
	print "w2:\n",sess.run(w2)