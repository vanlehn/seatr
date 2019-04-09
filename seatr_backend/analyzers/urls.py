from analyzers import views
from django.conf.urls import url

urlpatterns = [
    url(r'^simple$', views.AnalyzerSimple.as_view()),
]
