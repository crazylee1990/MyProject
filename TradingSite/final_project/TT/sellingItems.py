from django.shortcuts import render, redirect, get_object_or_404
from django.core.urlresolvers import reverse
from django.db import transaction
from django.core.exceptions import ObjectDoesNotExist

from django.contrib.auth.decorators import login_required

from django.contrib.auth.models import User
from django.contrib.auth import login, authenticate

from django.contrib.auth.tokens import default_token_generator
from django.core.mail import send_mail
from django.db.models import Q
from django.http import HttpResponse, Http404
from mimetypes import guess_type
import time
from TT.models import *
from TT.forms import *

TAGS ={"Books":1,"Clothing":2,"Electronics":3,"Food":4,"Toy":5}


@login_required
def displaySellingItem(request,id):
    context = {}
    item=SellingItem.objects.filter(id=id)
    if len(item)==0:
        context['error'] = "The item is deleted or not exist!"
        return render(request,'TT/error.html',context)
    context['sellingItem'] = item[0]
    context['doc'] = Document.objects.filter(user=item[0].user)[0]
    return render(request,'TT/displaySellingItem.html',context)

@login_required
@transaction.atomic
def editSellingItem(request,id):
    context = {}

    item=SellingItem.objects.filter(id=id)
    if len(item)==0:
        context['error'] = "The item is deleted or not exist!"
        return render(request,'TT/error.html',context)

    item=item[0]

    if request.user.id != item.user.id :
        context['error'] = "You cannot edit this item!"
        return render(request,'TT/error.html',context)

    if request.method == 'GET':
        form = SellingItemFrom(instance=item)
        context['form']=form
        context['id']=id
        return render(request,'TT/editSellingItem.html',context)

    postData = request.POST
    tag = postData['tag']

    if tag != ' ':
        item.tag=TAGS[tag]
        item.save()

    form = SellingItemFrom(request.POST,request.FILES,instance=item)

    if not form.is_valid():
        context['form']=form
        context['id']=id
        return render(request,'TT/editSellingItem.html',context)

    form.save()

    return redirect(reverse('displaySellingItem',args=[id]))

@login_required
@transaction.atomic
def postSellingItem(request):
    context = {}
    if request.method == 'GET':
        context['form'] = SellingItemFrom()
        return render(request,'TT/postSellingItem.html',context)

    # print "xx0"
    form = SellingItemFrom(request.POST)
    context['form'] = form

    if not form.is_valid():
    	# print "xx1"
        return render(request,"TT/postSellingItem.html",context)

    postData = request.POST
    tag = postData['tag']
    timeN = time.strftime('%Y-%m-%d %X')
    newItem = SellingItem(user=request.user,postTime = timeN,tag = TAGS[tag])
    itemForm = SellingItemFrom(request.POST,request.FILES, instance = newItem)

    if not itemForm.is_valid():
        context = {'form':itemForm }
        # print "xx2"
        return render(request,"TT/postSellingItem.html",context)

    itemForm.save()
    # print "xx3"
    return redirect(reverse('manage'))

# @login_required
def get_photo(request, id):
    item = SellingItem.objects.filter(id=id)
    # print "get_photo:"+str(id)+"   "+str(len(item))

    if len(item)==0:
        raise Http404

    item=item[0]
    # print "ok"
    if not item.pic:
        # print "not found"
        raise Http404
    # print "ok2"

    content_type = guess_type(item.pic.name)
    # print "ok3"
    return HttpResponse(item.pic, mimetype=content_type)

@login_required
def itemsByTag(request):
    context={}
    # print "ok1"
    getData = request.GET
    # print "ok2"
    tag = getData['tag']
    # print "ok3 t:"+tag
    context['items'] = SellingItem.objects.filter(tag = TAGS[tag],user=request.user)
    # print "ok4 l:"+str(len(context['items']))
    return render(request, 'TT/sellingItems.xml', context, content_type='application/xml'); 


def searchByTag(request,tag):
    context={}
    # # print "ok1"
    # getData = request.GET
    # # print "ok2"
    # tag = getData['tag']
    # print "ok3 t:"+tag
    context['items'] = SellingItem.objects.filter(tag = TAGS[tag])
    # print "ok4 l:"+str(len(context['items']))
    return render(request, 'TT/sellingItems.xml', context, content_type='application/xml'); 

def searchByName(request,keywords):
    context={}
    keys = keywords.split(' ')
    items=[]

    for key in keys:

        tmps=SellingItem.objects.filter(Q(name__contains=' '+key+' ')|Q(name__istartswith=key+' ')|Q(name__iendswith=' '+key))
        # tmps=SellingItem.objects.filter(name__contains=(key))

        for tmp in tmps:
            if tmp in items:
                continue
            else:
                items+=[tmp]

    if len(keys)==0 :
        items=SellingItem.objects.all()

    # print items

    context['items']=items
    return render(request, 'TT/sellingItems.xml', context, content_type='application/xml'); 
 
@login_required
@transaction.atomic
def delete(request,id):
    context={}
    item = SellingItem.objects.filter(id=id)

    if len(item)==0:
        context['error'] = "The item does not exist!"
        return render(request,'TT/error.html',context)

    item=item[0]

    if request.user.id != item.user.id:
        context['error'] = "You cannot delete this item!"
        return render(request,'TT/error.html',context)

    orders = Order.objects.filter(seller=request.user)

    for order in orders:
        if order.item.id == id:
            if order.getStatus() == 'accepted':
                context['error'] = "You cannot delete this item NOW! Some orders with this item are under transaction."
                return render(request,'TT/error.html',context)

    item.delete()   

    return redirect(reverse('manage'))