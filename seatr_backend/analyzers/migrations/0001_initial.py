# Generated by Django 2.1.5 on 2019-02-08 22:17

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('courses', '0001_initial'),
        ('students', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Analyzers',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('course', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='courses.Courses')),
                ('student', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='students.Students')),
            ],
        ),
    ]