from django.contrib.auth.models import Group, User
from rest_framework import serializers
from api.models import Contest, Photo, Vote


class UserSerializer(serializers.HyperlinkedModelSerializer):
    photos = serializers.SerializerMethodField()

    # This method retrieves the photos associated with the user.
    def get_photos(self, obj):
        request = self.context.get('request')
        photos = Photo.objects.filter(owner=obj)
        return [request.build_absolute_uri(photo.image.url) for photo in photos]

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'groups', 'photos']


class RoleSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Group
        fields = ['id', 'name']


class ContestSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Contest
        fields = ['id', 'name', 'start_date', 'end_date',
                  'voting_start_date', 'voting_end_date', 'max_photos_per_user']


class PhotoSerializer(serializers.HyperlinkedModelSerializer):
    owner_name = serializers.CharField(source='owner.username', read_only=True)
    uri = serializers.SerializerMethodField()
    # This method generates a URI for the photo.
    def get_uri(self, obj):
        request = self.context.get('request')
        return request.build_absolute_uri(f'/photos/{obj.id}/')

    class Meta:
        model = Photo
        fields = ['id', 'image', 'name', 'owner_name', 'owner','status', 'upload_date', 'votes', 'contest', 'uri']
        read_only_fields = ['owner_name', 'votes', 'uri']


class VoteSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Vote
        fields = ['id', 'user', 'photo', 'timestamp']
