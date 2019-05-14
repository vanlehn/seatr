from rest_framework                  import serializers
from rest_framework.authtoken.models import Token

from users.models import User, Platform


class UsersSerializer(serializers.ModelSerializer):
    class Meta:
        model  = User
        fields = '__all__'


# class TokenSerializer(serializers.ModelSerializer):
#     # user = UsersSerializer(read_only=True)
#     def getUser(self, obj):
#         uid = self.context['request'].POST["external_id"]
#         return UsersSerializer.data()
    
#     user = serializers.SerializerMethodField('getUser')
#     class Meta:
#         model  = Token
#         fields = ('user', )

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