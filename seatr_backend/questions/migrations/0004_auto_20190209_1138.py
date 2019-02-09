# Generated by Django 2.1.5 on 2019-02-09 11:38

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('questions', '0003_auto_20190208_2321'),
    ]

    operations = [
        migrations.AlterField(
            model_name='questionsstudentsmap',
            name='status',
            field=models.IntegerField(choices=[(1, 'attempted'), (2, 'seen'), (3, 'correct'), (4, 'incorrect')], default=2),
        ),
    ]