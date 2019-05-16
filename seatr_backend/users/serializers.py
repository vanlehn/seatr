from rest_framework                  import serializers
from rest_framework.authtoken.models import Token

from users.models import User, Platform


class UsersSerializer(serializers.ModelSerializer):
    class Meta:
        model  = User
        fields = '__all__'


class PlatformSerializer(serializers.ModelSerializer):
    class Meta:
        model  = Platform
        fields = ("username", "password")
    
    def create(self, validated_data):
        password = validated_data.pop('password')
        platform = Platform(**validated_data)
        platform.set_password(password)
        platform.save()
        return platform
    
    # TODO: doesn't work for some reason, says PUT not allowed
    def update(self, instance, validated_data):
        password = validated_data.pop('new_password')
        # platform = Platform.objects.get(validated_data.get('username'))
        instance.set_password(password)
        instance.save()
        return instance