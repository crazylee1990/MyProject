from django import forms

from django.contrib.auth.models import User
from models import *


class RegistrationForm(forms.Form):
    username = forms.CharField(max_length = 50,
    						   label='username',
                               widget=forms.TextInput(
                                attrs={'label':'username',
                                       'class':'form-control',
                                       'placeholder':'Username',
                                       'required':True,
                                       'autofocus':True}))
    firstname = forms.CharField(max_length = 50,
                               label='firstname',
                               widget=forms.TextInput(
                                attrs={'label':'email',
                                       'class':'form-control',
                                       'placeholder':'First Name',
                                       'required':True}))
    lastname = forms.CharField(max_length = 50,
                               label='lastname',
                               widget=forms.TextInput(
                                attrs={'label':'email',
                                       'class':'form-control',
                                       'placeholder':'Last Name',
                                       'required':True}))
    email=forms.CharField(max_length=70,
    					  label='email',
                           widget=forms.EmailInput(
                            attrs={'label':'email',
                                   'class':'form-control',
                                   'placeholder':'E-mail',
                                   'required':True}))
    password1 = forms.CharField(max_length = 200, 
                                label='Password', 
                                widget = forms.PasswordInput(
                                attrs={'label':'password1',
                                   'class':'form-control',
                                   'placeholder':'Password',
                                   'required':True}))
    password2 = forms.CharField(max_length = 200, 
                                label='Confirm password',  
                                widget = forms.PasswordInput(
                                attrs={'label':'password2',
                                   'class':'form-control',
                                   'placeholder':'Confirm Password',
                                   'required':True}))

    def clean(self):
      cleaned_data = super(RegistrationForm,self).clean()

    	# if not '@' in cleaned_data.get('email'):
    	# 	raise forms.ValidationError("Email address in invailid.")
      password1 = cleaned_data.get('password1')
      password2 = cleaned_data.get('password2')
      if password1 and password2 and password1 != password2:
          raise forms.ValidationError("Passwords did not match.")

      return cleaned_data

    def clean_username(self):
	    username = self.cleaned_data.get('username')
	    if User.objects.filter(username__exact=username):
	        raise forms.ValidationError("Username is already taken.")

	    return username

class SellingItemFrom(forms.ModelForm):
  class Meta:
    model = SellingItem
    exclude = ('user', 'tag')
    widgets = {'pic' : forms.FileInput(),
               'name' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True}),
               'price' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True,
                                              'type':'number'}),
               'description' : forms.Textarea(attrs={'cols': 80,
                                                  'rows': 20,
                                                  'class': 'form-control',
                                                  'required':True,
                                                  'style':'width:400px; height:100px;',
                                                  'placeholder':'Type in your description'}) }
  def clean(self):
    cleaned_data = super(SellingItemFrom,self).clean()
    price = cleaned_data.get('price')

    if price<=0:
      raise forms.ValidationError("price should be greater than zero")
    return cleaned_data


class WishingItemFrom(forms.ModelForm):
  class Meta:
    model = WishingItem
    exclude = ('user', 'tag','postTime')
    widgets = {'pic' : forms.FileInput(),
              'num' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True}),
              'upPrice' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True,
                                              'type':'number'}),
              'downPrice' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True,
                                              'type':'number'}),
              'name' : forms.TextInput(attrs={'class': 'form-control',
                                              'required':True}),
               'description' : forms.Textarea(attrs={'cols': 80,
                                                  'rows': 20,
                                                  'class': 'form-control',
                                                  'required':True,
                                                  'style':'width:400px; height:100px;',
                                                  'placeholder':'Type in your description'})
              }
  def clean(self):
    cleaned_data = super(WishingItemFrom,self).clean()
    up = cleaned_data.get('upPrice')
    down = cleaned_data.get('downPrice')

    if up<=0 or down<=0:
      raise forms.ValidationError("price should be greater than zero")

    if up<down :
      raise forms.ValidationError("down price is higher than up price.")

    return cleaned_data

class OrderForm(forms.ModelForm):
  class Meta:
    model = Order
    exclude = ('buyer','transportFee','seller','item','status','totalPrice','totalNum','wishingItem','expireTime')
    widgets = {'pic' : forms.FileInput(),
               'address1' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'address2' : forms.TextInput(attrs={'class': 'form-control'}),
               'city' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'state' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'zipcode' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'country' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               # 'transportFee' : forms.TextInput(attrs={'class': 'form-control',
               #                                    'required':True}),
               'reciepient' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'cell' : forms.TextInput(attrs={'class': 'form-control',
                                                  'required':True}),
               'expireTime': forms.TextInput(attrs={'class': 'form-control',
                                                    'type':'number'
                                                  })
              }

    def clean_expireTime(self):
      expireTime = self.cleaned_data.get('expireTime')
      if expireTime<=0:
          raise forms.ValidationError("Expire time should be greater than 0 day.")



  # def clean(self):
  #   cleaned_data = super(OrderForm,self).clean
  #   # totalPrice = cleaned_data.get('totalPrice')
  #   # transportFee = cleaned_data.get('transportFee')
  #   state = cleaned_data.get('state')
  #   # if totalPrice<0:
  #   #   raise forms.ValidationError("totalPrice should be greater than zero")
  #   # if transportFee<0:
  #   #   raise forms.ValidationError("transportFee should be greater than zero")

  #   if state == "":
  #     raise forms.ValidationError("need to select a state")


  #   return cleaned_data





