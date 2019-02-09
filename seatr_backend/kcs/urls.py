from kcs import views
from django.conf.urls import url

urlpatterns = [
    url(r'^create-kc/$', views.ListCreateKCs.as_view()),
    url(r'^list-kcs/$', views.ListCreateKCs.as_view()),
]