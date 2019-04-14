from testing import views
from django.conf.urls import url

urlpatterns = [
    url(r'^get-test/$', views.GetPostTest.as_view()),
    url(r'^post-test/$', views.GetPostTest.as_view()),
]