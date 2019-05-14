from django.db                       import models
from django.conf                     import settings
from django.contrib.auth.models      import AbstractUser
from rest_framework.authtoken.models import Token
from django.dispatch                 import receiver
from django.db.models.signals        import post_save

ROLES =(
    (0, "user"),
    (1, "professor"),
    (2, "admin")
)
class User(models.Model):
    external_id = models.IntegerField(primary_key=True, null=False, unique=True)
    role        = models.IntegerField(choices=ROLES, null=False, default=0)
    

@receiver(post_save, sender=settings.AUTH_USER_MODEL)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)
class Platform(AbstractUser):
    pass





    