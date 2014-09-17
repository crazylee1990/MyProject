from django.db import models
from django import forms
from django.contrib.auth.models import User
import datetime,time

STATUS = {'normal':0,'accepted':1,'refused':2,'expired':3,'completed':4,
			0:'normal',1:'accepted',2:'refused',3:'expired',4:'completed'}

TAGS ={" ":0,"Books":1,"Clothing":2,"Electronics":3,"Food":4,"Toy":5,
		0:" ",1:"Books",2:"Clothing",3:"Electronics",4:"Food",5:"Toy"}

# class Buyer(models.Model):
# 	user = models.ForeignKey(User)
# 	orders = models.ManyToManyField("Order",related_name="order-buy")


# 	def _unicode_(self):
# 		return 'Buyer:'+self.user.email

# class Seller(models.Model):
# 	user = models.ForeignKey(User)
# 	orders = models.ManyToManyField("Order",related_name="order-sell")


# 	def _unicode_(self):
# 		return 'Seller:'+self.user.email

class Wallet(models.Model):
	user = models.ForeignKey(User)
	balance = models.IntegerField(default=1000)
	pending = models.IntegerField(default=0)
	availabe = models.IntegerField(default=1000)

	def _unicode_(self):
		return 'Wallet:'+self.user.email

# class Bank(models.Model):


# 	def _unicode_(self):
# 		return ""

class Transaction(models.Model):
	order = models.ForeignKey("Order")

	def create(self):
		buyerW=Wallet.objects.filter(user=self.order.buyer)[0]
		# sellerW=Wallet.objects.filter(user=self.order.seller)

		if buyerW.availabe < self.order.totalPrice:
			return -1

		buyerW.pending+=self.order.totalPrice
		buyerW.availabe-=self.order.totalPrice
		buyerW.save()
		return 0

	def complete(self):
		buyerW=Wallet.objects.filter(user=self.order.buyer)[0]
		sellerW=Wallet.objects.filter(user=self.order.seller)[0]

		buyerW.pending-=self.order.totalPrice
		buyerW.balance-=self.order.totalPrice
		buyerW.save()
		sellerW.availabe+=self.order.totalPrice
		sellerW.balance+=self.order.totalPrice
		sellerW.save()

		buyerDoc = Document.objects.filter(user=self.order.buyer)[0]
		sellerDoc = Document.objects.filter(user=self.order.seller)[0]

		buyerDoc.addBuyerDoc(self.order.totalPrice)
		sellerDoc.addSellerDoc(self.order.totalPrice)

		return

	def _unicode_(self):
		return ""

class WishingItem(models.Model):
	user = models.ForeignKey(User)
	name = models.CharField(max_length = 200)
	tag = models.IntegerField()
	description = models.CharField(max_length = 2000)
	# images = models.ManyToManyField("ImageDesc")
	pic = models.ImageField(upload_to="TT-pics", blank=True)
	num = models.IntegerField()
	upPrice = models.IntegerField()
	downPrice = models.IntegerField()
	postTime = models.CharField(max_length=20,default="", blank=True)

	def getTag(self):
		return TAGS[self.tag]

	def _unicode_(self):
		return self.name

class SellingItem(models.Model):
	user = models.ForeignKey(User)
	name = models.CharField(max_length = 200)
	tag = models.IntegerField()
	description = models.CharField(max_length = 2000)
	# images = models.ManyToManyField("ImageDesc")
	pic = models.ImageField(upload_to="TT-pics", blank=True)
	price = models.IntegerField()	
	postTime = models.CharField(max_length=20,default="", blank=True)

	def getTag(self):
		return TAGS[self.tag]

	def _unicode_(self):
		return self.name

class Document(models.Model):
	user = models.ForeignKey(User)
	# records = models.ManyToManyField("DocumentValue")
	timesbuy = models.IntegerField(default=0)
	pricebuy = models.IntegerField(default=0)

	timessell = models.IntegerField(default=0)
	pricesell = models.IntegerField(default=0)

	editTime = models.DateTimeField(auto_now=True)

	def addBuyerDoc(self,price):
		self.timesbuy+=1
		self.pricebuy+=price
		self.save()
		return

	def addSellerDoc(self,price):
		self.timessell+=1
		self.pricesell+=price
		self.save()
		return

	def _unicode_(self):
		return 'Document:'+self.user.email


class DocumentValue(models.Model):
	doc = models.ForeignKey(Document)
	tag = models.IntegerField()
	orderNum = models.IntegerField()
	totalPrice = models.IntegerField()

	def _unicode_(self):
		return self.orderNum+" :"+self.totalPrice

class ImageDesc(models.Model):
	pic = models.ImageField(upload_to="TT-pics", blank=True)
	description = models.CharField(max_length=200)

	def _unicode_(self):
		return self.description


class UserToken(models.Model):
	user = models.ForeignKey(User)
	token = models.CharField(max_length=200)
	def __unicode__(self):
		return self.token 

class Order(models.Model):
	# buyer = models.ForeignKey(Buyer,related_name="order-buy")
	# seller = models.ForeignKey(Seller,related_name="order-sell")
	
	buyer = models.ForeignKey(User,related_name="order-buy")
	seller = models.ForeignKey(User,related_name="order-sell")
	

	# buyer = models.ForeignKey(User)
	# seller = models.ForeignKey(User)
	item = models.ForeignKey(SellingItem)
	wishingItem = models.ForeignKey(WishingItem,blank=True)
	# items = models.ManyToManyField(SellingItem)

	# acceptState = models.BooleanField(default=False)
	status = models.IntegerField(default=STATUS['normal'])

	address1 = models.CharField(max_length = 200, blank=True)
	address2 = models.CharField(max_length = 200, blank=True)
	city = models.CharField(max_length = 20, blank=True)
	state = models.CharField(max_length = 2, blank=True)
	zipcode = models.CharField(max_length = 5, blank=True)
	country = models.CharField(max_length = 20, blank=True)
	totalPrice = models.IntegerField(max_length = 20)
	totalNum = models.IntegerField(max_length = 20)
	transportFee = models.IntegerField(max_length = 20,blank=True)
	reciepient = models.CharField(max_length = 50, blank=True)
	cell = models.CharField(max_length = 10, blank=True)

	createTime = models.DateField(auto_now_add=True)
	expireTime = models.IntegerField(max_length = 20,default=30)


	def _unicode_(self):
		return ""

	def checkExpiration(self):
		if self.status !=  STATUS['normal']:
			return
		today = datetime.datetime.now().date()
		if (today - self.createTime).days > self.expireTime:
			self.status = STATUS['expired']
			self.save()
		return

	def refuse(self):
		if self.status !=  STATUS['normal']:
			return
		self.status=STATUS['refused']
		self.save()
		return

	def accept(self):
		if self.status !=  STATUS['normal']:
			return
		self.status=STATUS['accepted']
		self.save()
		return

	def complete(self):
		if self.status !=  STATUS['accepted']:
			return
		self.status=STATUS['completed']
		self.save()
		return

	def isExpired(self):
		return self.status==STATUS['expired']

	def isRefused(self):
		return self.status==STATUS['refused']

	def isAccepted(self):
		return self.status==STATUS['accepted']

	def getStatus(self):
		return STATUS[self.status];

	def getExpireDate(self):
		date=self.createTime+datetime.timedelta(self.expireTime)
		return date.strftime('%Y-%m-%d')


class Test2(models.Model):
	val=models.CharField(max_length = 20,default="dd")

	def _unicode_(self):
		return str(self.id)

class Test(models.Model):

	key=models.ForeignKey(Test2)

	def _unicode_(self):
		return ""

