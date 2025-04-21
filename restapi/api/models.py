from django.db import models

# Create your models here.

from django.db import models
import uuid

STATUS_CHOICES = {
    'Pending': 'Pending',
    'Approved': 'Approved',
    'Rejected': 'Rejected',
}

"""
This model represents a photography contest.

id - A unique identifier for the contest, automatically generated using UUID.
name - The name of the contest, can be established by the user.
start_date - The date and time when the contest starts.
end_date - The date and time when the contest ends.
voting_start_date - The date and time when the voting starts.
voting_end_date - The date and time when the voting ends.
max_photos_per_user - The maximum number of photos a user can submit to the contest.

"""


class Contest(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    name = models.CharField(max_length=100, blank=False,
                            default='Contest Name')
    start_date = models.DateTimeField(blank=False)
    end_date = models.DateTimeField(blank=False)
    voting_start_date = models.DateTimeField(blank=False)
    voting_end_date = models.DateTimeField(blank=False)

    def clean(self):
        from django.core.exceptions import ValidationError
        if self.start_date >= self.end_date:
            raise ValidationError(
                "The contest start date must be before the end date.")
        if self.voting_start_date >= self.voting_end_date:
            raise ValidationError(
                "The voting start date must be before the voting end date.")
        if self.voting_start_date < self.end_date:
            raise ValidationError(
                "The voting start date must be after the contest end date.")

    max_photos_per_user = models.IntegerField(blank=False, default=0)

    class Meta:
        ordering = ['name']

    def __str__(self):
        return self.name


"""
This model represents a photo submitted to a contest.

id - A unique identifier for the photo, automatically generated using UUID.
image - The image file of the photo, stored in a specific upload path.
name - The name of the photo, can be established by the user.
owner - The user who submitted the photo, linked to the User model.
status - The status of the photo, can be 'Pending', 'Approved', or 'Rejected'.
upload_date - The date and time when the photo was uploaded.
votes - The number of votes the photo has received.
contest - The contest to which the photo belongs, linked to the Contest model.

"""


class Photo(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    """
    Method used to generate the upload path for the photo.
    The path will be "photos/<lowercase name>.<uppercase extension>"
    """
    def upload_to(instance, filename):
        import os
        from django.utils.text import slugify
        base, ext = os.path.splitext(filename)
        return f'photos/{slugify(base).lower()}{ext.upper()}'

    image = models.ImageField(upload_to=upload_to)
    name = models.CharField(max_length=100, blank=False, default='Photo Name')
    owner = models.ForeignKey(
        'auth.User', on_delete=models.CASCADE, editable=False)
    status = models.CharField(max_length=20, blank=False,
                              choices=STATUS_CHOICES, default=STATUS_CHOICES["Pending"])
    upload_date = models.DateTimeField(auto_now_add=True)

    @property
    def votes(self):
        return Vote.objects.filter(photo=self).count()
    contest = models.ForeignKey(Contest, on_delete=models.CASCADE)

    class Meta:
        ordering = ['upload_date']

    def __str__(self):
        return f'{self.name} - {self.owner.username}({self.contest.name})'


"""
This model represents a vote cast by a user for a photo.

id - A unique identifier for the vote, automatically generated using UUID.
user - The user who cast the vote, linked to the User model.
photo - The photo that received the vote, linked to the Photo model.
timestamp - The date and time when the vote was cast.

"""


class Vote(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey('auth.User', on_delete=models.CASCADE)
    photo = models.ForeignKey(Photo, on_delete=models.CASCADE)
    timestamp = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['timestamp']

    def __str__(self):
        return self.user + ' - ' + self.photo + ' - '
