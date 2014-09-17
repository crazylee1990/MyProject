from django.conf.urls import patterns, url
from blog.models import *

urlpatterns = patterns('',
    url(r'^$', 'blog.views.home'),
    url(r'^add-item$', 'blog.views.add_item'),
    url(r'^delete-item/(?P<id>\d+)$', 'blog.views.delete_item'),
    # Route for built-in authentication with our own custom login page
    url(r'^login$', 'django.contrib.auth.views.login', {'template_name':'blog/login.html'}),
    # Route to logout a user and send them back to the login page
    url(r'^logout$', 'django.contrib.auth.views.logout_then_login'),
    url(r'^register$', 'blog.views.register'),
    #url(r'^%s/confirmation$' % (Item.confirmation_key), 'blog.views.confirmation')
    url(r'^confirmation/(?P<confirmation_key>\w+)$', 'blog.views.confirmation'),
    url(r'^photo/(?P<item_id>\d+)$', 'blog.views.photo'),
    #url(r'^visitorPage$', 'blog.views.visitor'),
    url(r'^visitorPage/(?P<user_firstname>\w+)$','blog.views.visitor'),
    url(r'^manage$','blog.views.manage'),
    url(r'^postings/(?P<user_firstname>\w+)$', 'blog.views.viewOthers'),
)
