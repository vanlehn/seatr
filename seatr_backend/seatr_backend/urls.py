"""seatr_backend URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include

from rest_framework.authtoken.views import obtain_auth_token

urlpatterns = [
    path('admin/', admin.site.urls),
    path(r'analyzers/', include('analyzers.urls')),
    path(r'users/', include('users.urls')),
    path(r'questions/', include('questions.urls')),
    path(r'courses/', include('courses.urls')),
    path(r'testing/', include('testing.urls')),
    # post endpoint to get token for given username and password
    # http post http://127.0.0.1:8000/api-token-auth/ username=vitor password=123
    path('api-token-auth/', obtain_auth_token, name='api_token_auth'),
]
