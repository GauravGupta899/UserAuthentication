

from django.urls import path
from .views import registration,login,dashboard

urlpatterns = [
    
    path('register/', registration),
    path('login/', login),
    path('dashboard/', dashboard),
]
