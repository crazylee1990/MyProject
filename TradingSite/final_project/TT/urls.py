from django.conf.urls import patterns, include, url


from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',

    url(r'^$', 'TT.views.home', name = 'home'),

    url(r'^login$','django.contrib.auth.views.login',{'template_name':'TT/login.html'},name='login'),
    url(r'^logout$','django.contrib.auth.views.logout_then_login',name='logout'),
    url(r'^register$','TT.views.register',name='register'),
    url(r'^confirm$','TT.views.confirmEmail',name='confirm'),
    url(r'^wallet$','TT.views.viewWallet',name='wallet'),

    url(r'^manage$', 'TT.views.personalPage',name='manage'),
    url(r'^contact$','TT.views.contact',name='contact'),
    url(r'^about$','TT.views.about',name='about'),

    url(r'^home/viewWishingItem/(?P<id>\d+)$','TT.home.viewWishingItem',name='viewWishingItem'),
    url(r'^home/viewSellingItem/(?P<id>\d+)$','TT.home.viewSellingItem',name='viewSellingItem'),

    url(r'^order/createOrder/(?P<wishID>\d+)$','TT.order.createOrder',name='createOrder'),
    url(r'^order/postWithNewItem$','TT.order.postWithNewItem',name='postWithNewItem'),
    url(r'^order/postWithExistItem$','TT.order.postWithExistItem',name='postWithExistItem'),
    url(r'^order/viewAsSeller/(?P<id>\d+)$','TT.order.viewAsSeller',name='viewOrderAsSeller'),
    url(r'^order/viewAsBuyer/(?P<id>\d+)$','TT.order.viewAsBuyer',name='viewOrderAsBuyer'),
    url(r'^order/accept/(?P<id>\d+)$','TT.order.accept',name='acceptOrder'),
    url(r'^order/refuse/(?P<id>\d+)$','TT.order.refuse',name='refuseOrder'),
    url(r'^order/complete/(?P<id>\d+)$','TT.order.complete',name='completeOrder'),


    url(r'^wishingItem/display/(?P<id>\d+)$','TT.wishingItems.displayWishingItem',name='displayWishingItem'),
    url(r'^wishingItem/post$','TT.wishingItems.postWishingItem',name='postWishingItem'),
    url(r'^wishingItem/edit/(?P<id>\d+)$','TT.wishingItems.editWishingItem',name='editWishingItem'),
    url(r'^wishingItem/photo/(?P<id>\d+)$','TT.wishingItems.get_photo',name='wishPhoto'),
    url(r'^wishingItem/searchByTag/(?P<tag>\w+)$','TT.wishingItems.searchByTag',name='searchWishingItemsByTag'),
    url(r'^wishingItem/searchByName/(?P<keywords>.*)$','TT.wishingItems.searchByName',name='searchWishingItemByName'),
    url(r'^wishingItem/delete/(?P<id>\d+)$','TT.wishingItems.delete',name='deleteWishingItem'),


    url(r'^sellingItem/display/(?P<id>\d+)$','TT.sellingItems.displaySellingItem',name='displaySellingItem'),
    url(r'^sellingItem/post$','TT.sellingItems.postSellingItem',name='postSellingItem'),
    url(r'^sellingItem/edit/(?P<id>\d+)$','TT.sellingItems.editSellingItem',name='editSellingItem'),
    url(r'^sellingItem/photo/(?P<id>\d+)$','TT.sellingItems.get_photo',name='sellPhoto'),
    url(r'^sellingItem/itemsByTag$','TT.sellingItems.itemsByTag',name='sellingItemsByTag'),
    url(r'^sellingItem/searchByName/(?P<keywords>.*)$','TT.sellingItems.searchByName',name='searchSellingItemByName'),
    url(r'^sellingItem/searchByTag/(?P<tag>\w+)$','TT.sellingItems.searchByTag',name='searchSellingItemsByTag'),
    url(r'^sellingItem/delete/(?P<id>\d+)$','TT.sellingItems.delete',name='deleteSellingItem'),


)

