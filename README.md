**STEPS TO INSTALL**
1. virtualenv venv
2. pip install -r requirements.txt

**DEV NOTES:**
*these are meant to help setup the software and solution to some of the possible issues that might arise
Deploying static files*

1. Before running nginx, you have to collect all Django static files in the static folder. First of all you have to edit *mysite/settings.py* `adding:STATIC_ROOT = os.path.join(BASE_DIR, "static/")` and then run `python manage.py collectstatic`
