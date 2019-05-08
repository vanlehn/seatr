from . import views
from django.conf.urls import url

urlpatterns = [
    url(r'^create-user/$', views.ListCreateUsers.as_view()),
    url(r'^list-users/$', views.ListCreateUsers.as_view()),
    url(r'^get-create-token/$', views.GetCreateTokenView.as_view()),
]