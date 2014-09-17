from django.shortcuts import render, HttpResponse, redirect,render_to_response,get_object_or_404
from django.core.exceptions import ObjectDoesNotExist

from django.core.mail import send_mail, BadHeaderError

# Decorator to use built-in authentication system
from django.contrib.auth.decorators import login_required

# Used to create and manually log in a user
from django.contrib.auth.models import User
from django.contrib.auth import login, authenticate

from blog.models import *
from django.core.mail import send_mail
import random,sha
from mimetypes import guess_type

@login_required
def home(request):
    items = []
    allUsers = User.objects.all()
    first_name = request.user.first_name
    last_name = request.user.last_name
    context = {'items' : items,'first_name':first_name,'last_name':last_name,'allUsers':allUsers}
    return render(request, 'blog/postings.html',context)


@login_required
def add_item(request):
    errors = []
    # if item is not a parameer or the content is void, show error information
    if not 'item' in request.POST or not request.POST['item']:
            errors.append('You must enter an item to add.')
    # if no error exists, create a new posting
    else:
        if 'picture' in request.FILES:
            print("done")
            new_item = Item(title=request.POST['item'], picture=request.FILES['picture'],user=request.user)
        else:
            new_item = Item(title=request.POST['item'],user=request.user)
        new_item.save()
    items = Item.objects.filter(user=request.user)
    context = {'items' : items, 'errors' : errors}
    return render(request, 'blog/manageBlog.html', context)
    

@login_required
def delete_item(request, id):
    errors = []
    # Deletes item if the logged-in user has an item matching the id
    try:
	item_to_delete = Item.objects.get(id=id, user=request.user)
	item_to_delete.delete()
    except ObjectDoesNotExist:
	errors.append('The item did not exist in your posting.')

    items = Item.objects.filter(user=request.user)
    context = {'items' : items, 'errors' : errors}
    return render(request, 'blog/postings.html', context)


def photo (request, item_id):
    item = get_object_or_404(Item, id=item_id)
    if not item.picture:
        raise Http404
    content_type = guess_type(item.picture.name)
    return HttpResponse(item.picture.read(), content_type=content_type) 

def visitor(request,user_firstname):
    allUsers = User.objects.all()
    if (user_firstname == 'guest'):
        chosen_user = None
        items = ''
        
    else:
        chosen_user = User.objects.filter( first_name= user_firstname)
        items = Item.objects.filter( user = chosen_user).order_by('-pub_date')
    context = {'allUsers': allUsers, 'items' : items, 'chosen_user':chosen_user}   
    return render(request, 'blog/visitorPage.html', context)

@login_required
def manage(request):
    # Sets up just the logged-in user's postings
    items = Item.objects.filter( user = request.user).order_by('-pub_date')
    return render(request, 'blog/manageBlog.html', {'items' : items, 'user':request.user})

@login_required
def viewOthers(request,user_firstname):
    items = []
    allUsers = User.objects.all()
    first_name = request.user.first_name
    last_name = request.user.last_name
    currentUser = request.user
    currentfollow = Follow.objects.filter(login_user = currentUser, follow_user = user_firstname)
    # if the current follow does not exist, which means the login_user have not followed 
    # this user, then follow him
    if currentfollow.__len__() == 0:
        newfollow = Follow(login_user = currentUser, follow_user = user_firstname)
        newfollow.save()
    # else if the current follow has existed, then the login_user does not follow him anymore
    else:
        deletefollow = Follow.objects.get(login_user = currentUser, follow_user = user_firstname)
        deletefollow.delete()
    
    #get all objects whose login_user is current User
    follows = Follow.objects.filter(login_user = currentUser)
    for follow in follows:
        #print(follow.follow_user)
        followuser = User.objects.filter(first_name = follow.follow_user)
        followuser_item = Item.objects.filter(user= followuser)
        for item in followuser_item:
            items.append(item)
    items.sort(key=lambda object: object.pub_date, reverse=True)
    context = {'items' : items,'first_name':first_name,'last_name':last_name,'allUsers':allUsers}
    return render(request, 'blog/postings.html',context)









def register(request):
    context = {}
    # Just display the registration form if this is a GET request
    if request.method == 'GET':
            return render(request, 'blog/register.html', context)
    errors = []
    context['errors'] = errors

    # Checks the validity of the form data
    if not 'username' in request.POST or not request.POST['username']:
            errors.append('Username is required.')
    else:
            # Save the username in the request context to re-fill the username
            # field in case the form has errrors
            context['username'] = request.POST['username']
    if not 'firstName' in request.POST or not request.POST['firstName']:
            errors.append('Fist Name is required!')
    if not 'lastName' in request.POST or not request.POST['lastName']:
            errors.append('Last Name is required')
    if not 'password1' in request.POST or not request.POST['password1']:
            errors.append('Password is required.')
    if not 'password2' in request.POST or not request.POST['password2']:
            errors.append('Confirm password is required.')
    # make sure password1 and password2 are the same
    if 'password1' in request.POST and 'password2' in request.POST \
       and request.POST['password1'] and request.POST['password2'] \
       and request.POST['password1'] != request.POST['password2']:
            errors.append('Passwords did not match.')
    # check if the uesr has existed
    if len(User.objects.filter(username = request.POST['username'])) > 0:
            errors.append('Username is already taken.')
    if errors:
            return render(request, 'blog/register.html', context)

    # Creates the new user from the valid form data
    new_user = User.objects.create_user(username=request.POST['username'], \
                                    password=request.POST['password1'],\
                                    first_name = request.POST['firstName'],\
                                    last_name = request.POST['lastName'])
    new_user.is_active = False
    new_user.save()

    # Generate a random confirmation key and save user profile
    temp_key = sha.new(str(random.random())).hexdigest()[:5]
    confirmation_key = sha.new(temp_key + new_user.username).hexdigest()
    new_profile = Item(user = new_user,confirmation_key = confirmation_key,is_confirmed = 1)
    new_profile.save()

    # Then we will send a email to the user
    title = "My blog account confirmation"
    content = "Please click the following link to activate your accout \
    http://localhost:8000/blog/confirmation/%s" % (new_profile.confirmation_key)
    email = request.POST['username']
    send_mail(title, content,'chaol1@andrew.cmu.edu',[email], fail_silently=True)
    context ={'Reminder':'We have sent an email to your mailbox, please go for confirmation','Need_Confirmed': True}
    return render_to_response('blog/login.html',context)

       
# User gets email, passes the confirmation_key back to your server
def confirmation (request, confirmation_key):
    # if the user has logged on, we do not need to confirm the account anymore
    if request.user.is_authenticated():
        return render_to_response('blog/postings.html')
    
    user_profile = get_object_or_404(Item,confirmation_key = confirmation_key)
    #check if the confirmation key is the same with the record in my server,
    #then the user finishes the registration
    if(user_profile.confirmation_key == confirmation_key):
        user_profile.is_confirmed = True
        user = user_profile.user
        user.is_active = True
        user.save()
    else:
        user_profile.is_confirmed = True
    context = {'is_confirmed':user_profile.is_confirmed}
    return render_to_response('blog/e-mail-confirmation.html',context)
    
