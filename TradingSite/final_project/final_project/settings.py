"""
Django settings for final_project project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.realpath(os.path.dirname(__file__)) + '/'

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'o8kc9o=v9m6v$s4mszol4s3)oo)_m87x$xa%4ixpmegk*yqp)g'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []


# -------------------------Application---------------------
INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'TT',
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    # 'django.middleware.transaction.TransactionMiddleware',
)

#--------------login----------------------
LOGIN_URL = '/TT/login'
# Default URL to redirect to after a user logs in.
LOGIN_REDIRECT_URL = '/TT/'

#-----------------email--------------------
# EMAIL_BACKEND='django.core.mail.backends.console.EmailBackend'
EMAIL_HOST='smtp.gmail.com'
EMAIL_HOST_USER = 'tripleltradingcom'
EMAIL_HOST_PASSWORD='sui15637yi'
EMAIL_USE_TLS=True


#-------------------------------------------
ROOT_URLCONF = 'final_project.urls'

WSGI_APPLICATION = 'final_project.wsgi.application'


#-------------------Database-------------------
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': BASE_DIR+'db/TT.sqlite3',
        'USER': '',
        'PASSWORD': '',
        'HOST': '',
        'PORT': '', 
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/
LANGUAGE_CODE = 'en-us'
TIME_ZONE = 'US/Eastern'
USE_I18N = True
USE_L10N = True
USE_TZ = True


#------------media files------------
MEDIA_ROOT = BASE_DIR + 'media/'
MEDIA_URL = '/media/'

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/
STATIC_ROOT = 'static/'
STATIC_URL = '/static/'

STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
)

#-------------template-------------
TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.Loader',
    'django.template.loaders.app_directories.Loader',
)

