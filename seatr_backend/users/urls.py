from . import views
from django.conf.urls import url, include
from rest_framework   import routers

router = routers.DefaultRouter()
router.register(r'create-platform', views.CreatePlatform)

urlpatterns = [
    url(r'^create-user/$', views.ListCreateUsers.as_view()),
    url(r'^list-users/$', views.ListCreateUsers.as_view()),
    url(r'^', include(router.urls)),
]