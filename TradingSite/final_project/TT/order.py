from django.shortcuts import render, redirect, get_object_or_404
from django.core.urlresolvers import reverse
from django.db import transaction
from django.core.exceptions import ObjectDoesNotExist

from django.contrib.auth.decorators import login_required

from django.contrib.auth.models import User
from django.contrib.auth import login, authenticate

from django.contrib.auth.tokens import default_token_generator
from django.core.mail import send_mail

from django.http import HttpResponse, Http404
from mimetypes import guess_type
import time
from TT.models import *
from TT.forms import *


TAGS ={" ":0,"Books":1,"Clothing":2,"Electronics":3,"Food":4,"Toy":5}


@login_required
@transaction.atomic
def postWithExistItem(request):
    context={}
    postData = request.POST

    wishID = postData['wishID']
    sellID = postData['sellID']
    totalPrice = postData['totalPrice']
    totalNum = postData['totalNum']
    expireTime = postData['expireTime']

    wishingItem = WishingItem.objects.filter(id=wishID)
    sellingItem = SellingItem.objects.filter(id=sellID)

    if len(wishingItem)==0:
        context['error'] = "The wishingItem is deleted or not exist!"
        return render(request,'TT/error.html',context)
    if len(sellingItem)==0:
        context['error'] = "The sellingItem is deleted or not exist!"
        return render(request,'TT/error.html',context)

    wishingItem = wishingItem[0]
    sellingItem = sellingItem[0]

    seller = request.user
    buyer = wishingItem.user

    if seller.id==buyer.id :
        context['error'] = "You cannot sell to yourself"
        return render(request,'TT/error.html',context)

    newOrder = Order(buyer=buyer, seller=seller, item=sellingItem, wishingItem=wishingItem,
        totalNum=totalNum,totalPrice=totalPrice,expireTime=expireTime,transportFee=0)
    newOrder.save();

    return redirect(reverse('manage'))



@login_required
@transaction.atomic
def postWithNewItem(request):
    context = {}
    form = SellingItemFrom(request.POST)
    context['form'] = form
    postData = request.POST
    wishID = postData['wishID']
    wishingItem = WishingItem.objects.filter(id=wishID)

    if len(wishingItem)==0:
        context['error'] = "The wishingItem is deleted or not exist!"
        return render(request,'TT/error.html',context)
    
    wishingItem=wishingItem[0]
    context['wishingItem']=wishingItem

    totalPrice = postData['totalPrice']
    totalNum = postData['totalNum']
    expireTime = postData['expireTime']

    if totalPrice <0:
        context['totalPrice'] = totalPrice
        context['totalNum'] =totalNum
        context['error_totalPrice'] = "totalPrice should greater than zero"
        return render(request,'TT/createOrder.html',context)

    if totalNum <0:
        context['totalPrice'] = totalPrice
        context['totalNum'] = totalNum
        context['error_totalPrice'] = "totalNum should greater than zero"
        return render(request,'TT/createOrder.html',context)

    if not form.is_valid():
        # print "xx1"
        return render(request,'TT/createOrder.html',context)

    postData = request.POST
    tag = postData['tag']
    timeN = time.strftime('%Y-%m-%d %X')
    newItem = SellingItem(user=request.user,postTime = timeN,tag = TAGS[tag])
    itemForm = SellingItemFrom(request.POST,request.FILES, instance = newItem)

    if not itemForm.is_valid():
        context = {'form':itemForm }
        # print "xx2"
        return render(request,'TT/createOrder.html',context)

    itemForm.save()

    context={}
    postData = request.POST

    wishID = postData['wishID']
    # sellID = newItem.id

    # wishingItem = WishingItem.objects.filter(id=wishID)[0]
    sellingItem = newItem

    seller = request.user
    buyer = wishingItem.user

    if seller.id==buyer.id :
        context['error'] = "You cannot sell to yourself"
        return render(request,'TT/error.html',context)

    # print "totp:"+str(totalPrice)
    newOrder = Order(buyer=buyer, seller=seller, item=sellingItem, wishingItem=wishingItem,
        totalNum=totalNum,totalPrice=totalPrice,expireTime=expireTime,transportFee=0)

    newOrder.save();

    return redirect(reverse('manage'))


@login_required
def viewAsBuyer(request,id):
    context = {}

    order = Order.objects.filter(id=id)
    if len(order)==0 :
        context['error'] = "Order does not exist!"
        return render(request,'TT/error.html',context)
    order=order[0]

    context['type'] = "buyer"

    if request.user.id != order.buyer.id :
    	context['error'] = "You cannot view this order as a buyer!"
    	return render(request,'TT/error.html',context)

    context['selling']=True
    context['wishing']=True

    if order.item.id==None:
        context['selling']=False
    if order.wishingItem.id==None:
        context['wishing']=False

    order.checkExpiration()
    context['orderStatus'] = order.getStatus()
    context['order'] = order
    
    context['doc'] = Document.objects.filter(user=order.seller)[0]


    return render(request,'TT/viewOrder.html',context)

@login_required
def viewAsSeller(request,id):
    context = {}
    order = Order.objects.filter(id=id)
    if len(order)==0 :
        context['error'] = "Order does not exist!"
        return render(request,'TT/error.html',context)
    order=order[0]


    context['type'] = "seller"

    if request.user.id != order.seller.id :
    	context['error'] = "You cannot view this order as a seller!"
    	return render(request,'TT/error.html',context)

    context['selling']=True
    context['wishing']=True

    if order.item.id==None:
        context['selling']=False
    if order.wishingItem.id==None:
        context['wishing']=False

    order.checkExpiration()
    context['orderStatus'] = order.getStatus()
    context['order'] = order

    context['doc'] = Document.objects.filter(user=order.buyer)[0]

    return render(request,'TT/viewOrder.html',context)

@login_required
@transaction.atomic
def createOrder(request, wishID):
    context = {}

    wishingItem=WishingItem.objects.filter(id=wishID)

    if len(wishingItem)==0:
        context['error'] = "The wishingItem is deleted or not exist!"
        return render(request,'TT/error.html',context)

    wishingItem=wishingItem[0]

    if request.user.id==wishingItem.user.id :
        context['error'] = "You cannot sell to yourself"
        return render(request,'TT/error.html',context)


    context['wishingItem'] = wishingItem
    context['form'] = SellingItemFrom()
    # context['orderForm'] = OrderForm()
    return render(request,'TT/createOrder.html',context)


@login_required
@transaction.atomic
def accept(request, id):
    context={}
    order = Order.objects.filter(id=id)

    if len(order)==0 :
        context['error'] = "Order does not exist!"
        return render(request,'TT/error.html',context)

    order=order[0]
 
    if request.user.id != order.buyer.id:
        context['error'] = "You cannot accept this order!"
        return render(request,'TT/error.html',context)

    order.checkExpiration()
    if order.isExpired():
        context['error'] = "This Order is expired!"
        return render(request,'TT/error.html',context)

    if order.isRefused():
        context['error'] = "You refused this order earlier!"
        return render(request,'TT/error.html',context)

    if order.isAccepted():
        context['error'] = "You cannot accept this Order again!"
        return render(request,'TT/error.html',context)


    if request.method == 'GET':
        form = OrderForm(instance = order)
        context['form'] = form
        context['order'] = order
        return render(request,'TT/acceptOrder.html',context)


    form = OrderForm(request.POST,instance = order)

    if not form.is_valid():
        context['form'] = form
        context['order'] = order
        return render(request,'TT/acceptOrder.html',context)

    form.save()
    # order.acceptState=True
    order.save()
    order = Order.objects.filter(id=id)[0]

    t = Transaction(order=order)
    ret = t.create()

    if ret<0:
        context['error'] = "You do not have enough money in your account!"
        return render(request,'TT/error.html',context)

    order.accept()
    t.save()
    return redirect(reverse('viewOrderAsBuyer',args=[id]))


@login_required
@transaction.atomic
def refuse(request,id):
    context={}
    order = Order.objects.filter(id=id)

    if len(order)==0 :
        context['error'] = "Order does not exist!"
        return render(request,'TT/error.html',context)

    order=order[0]

    if request.user.id != order.buyer.id:
        context['error'] = "You cannot accept this order!"
        return render(request,'TT/error.html',context)

    order.checkExpiration()
    if order.isExpired():
        context['error'] = "This Order is expired!"
        return render(request,'TT/error.html',context)

    if order.isRefused():
        context['error'] = "You refused this order earlier!"
        return render(request,'TT/error.html',context)

    if order.isAccepted():
        context['error'] = "You cannot accept this Order again!"
        return render(request,'TT/error.html',context)

    order.refuse()
    return redirect(reverse('viewOrderAsBuyer',args=[id]))

# @login_required
# def cancel(request,id):
#     context={}
#     order = Order.objects.filter(id=id)

#     if len(order)==0 :
#         context['error'] = "Order does not exist!"
#         return render(request,'TT/error.html',context)

#     order=order[0]

#     if request.user.id != order.seller.id:
#         context['error'] = "You cannot accept this order!"
#         return render(request,'TT/error.html',context)

#     order.checkExpiration()
#     if order.isExpired():
#         context['error'] = "This Order is expired!"
#         return render(request,'TT/error.html',context)

#     if order.isRefused():
#         context['error'] = "You refused this order earlier!"
#         return render(request,'TT/error.html',context)

#     if order.isAccepted():
#         context['error'] = "You cannot accept this Order again!"
#         return render(request,'TT/error.html',context)

#     order.refuse()
#     return redirect(reverse('manage'))

@login_required
@transaction.atomic
def complete(request,id):
    context={}
    order = Order.objects.filter(id=id)

    if len(order)==0 :
        context['error'] = "Order does not exist!"
        return render(request,'TT/error.html',context)

    order=order[0]

    if request.user.id != order.buyer.id:
        context['error'] = "You cannot complete this order!"
        return render(request,'TT/error.html',context)

    order.checkExpiration()
    if order.getStatus()!='accepted':
        context['error'] = "This Order cannot be complete!"
        return render(request,'TT/error.html',context)

    tran = Transaction.objects.filter(order=order)[0]
    tran.complete()
    order.complete()

    return redirect(reverse('viewOrderAsBuyer',args=[id]))