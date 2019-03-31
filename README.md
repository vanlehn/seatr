**STEPS TO INSTALL**
1. virtualenv venv
2. pip install -r requirements.txt

DEV Notes: These notes are meant to help developers while setting up the machine and running code (in MAC)
* if you want to run ope using nginx php-fpm will be required. It's a WGI for PHP. The config files for both php and php-fpm ie. *php.ini* and *php-fpm.conf* both are present, at the required locations ie. */private/etc/php.ini* and */private/etc/php-fpm.conf* and but they are named as */private/etc/php.ini.default* and */private/etc/php-fpm.conf.default*. So, to successfully run php and php-fpm, just copy these files ie.
    * ``` sudo cp /private/etc/php.ini.default       /private/etc/php.ini ```
    * ```sudo cp /private/etc/php-fpm.conf.default  /private/etc/php-fpm.conf ```
    * ``` php-fpm ``` this is the binary
* the php-fpm by default writes the logs in */usr/var/log/php-fpm.logs*. To change this go inside the */private/etc/php-fpm.conf* file and write ``` error_log=/var/log/php-fpm/php-fpm.log ```
* https://github.com/perusio/php-fpm-example-config: sample php-fpm file
