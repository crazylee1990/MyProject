from django.db import models
import datetime
from django.utils import timezone

# User class for built-in authentication module
from django.contrib.auth.models import User

class Item(models.Model):
    title = models.CharField(max_length = 160)
    user = models.ForeignKey(User)
    pub_date = models.DateTimeField(auto_now_add = True)
    confirmation_key = models.CharField(max_length = 40)
    is_confirmed = models.BooleanField(default = 0)
    picture = models.ImageField(upload_to='blog-photos', blank=True)

    def _unicode_(self):
        return self.text

class Follow(models.Model):
    login_user = models.ForeignKey(User)
    follow_user = models.CharField(max_length=300)
