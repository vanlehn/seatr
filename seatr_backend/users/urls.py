from . import views
from django.conf.urls import url

urlpatterns = [
    url(r'^create-student/$', views.ListCreateStudents.as_view()),
    url(r'^list-students/$', views.ListCreateStudents.as_view()),
]