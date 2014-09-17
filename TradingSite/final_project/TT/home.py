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


TAGS ={"Books":1,"Clothing":2,"Electronics":3,"Food":4,"Toys":5}


def viewWishingItem(request,id):
    context={}
    item = WishingItem.objects.filter(id=id)
    if len(item)==0:
        context['error'] = "The item does not exist!"
        return render(request,'TT/error.html',context)
    item = item[0]
    context['wishingItem'] =item
    context['doc'] = Document.objects.filter(user=item.user)[0]

    return render(request,'TT/viewWishingItem.html', context)

def viewSellingItem(request,id):
    context={}
    item = SellingItem.objects.filter(id=id)
    if len(item)==0:
        context['error'] = "The item does not exist!"
        return render(request,'TT/error.html',context)
    item = item[0]
    context['sellingItem'] =item
    context['doc'] = Document.objects.filter(user=item.user)[0]

    return render(request,'TT/viewSellingItem.html', context)