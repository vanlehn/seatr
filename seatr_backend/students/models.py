from django.db import models

# Create your models here.


class Students(models.Model):
    external_id = models.IntegerField(null=False, unique=True)

