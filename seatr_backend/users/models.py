from django.db                  import models
from django.conf                import settings
from django.contrib.auth.models import AbstractUser


class User(AbstractUser):
    # username by default is unique and can't be null, overwritten here
    username    = models.CharField(blank=True, null=True, max_length=150)
    email       = models.EmailField(null=True, blank=True)
    external_id = models.IntegerField(primary_key=True, null=False, unique=True)

    USERNAME_FIELD = 'external_id'
    # the fields that are required by the createsuperuser command, username is must
    REQUIRED_FIELDS = ['username', 'email']

    def __str__(self):
        return str(self.external_id) + " " + self.username +  " " + self.email


# additional fields specified here, for future use
ROLES =(
    (0, "user"),
    (1, "professor"),
    (2, "admin")
)
class UserProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='user_profile')
    role = models.IntegerField(choices=ROLES, null=False, default=0)


    