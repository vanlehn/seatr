from analyzers import views
from django.conf.urls import url

urlpatterns = [
    url(r'^analyzer/simple$', views.AnalyzerSimple.as_view()),
]
