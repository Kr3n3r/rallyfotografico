###

REQUISITOS:

###

Una vez clonado el repo:

py -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r .\requirements.txt
cd .\restapi\
python .\manage.py migrate

py .\manage.py loaddata .\mockup\mockup_groups\MOCK_DATA_GROUPS.json --format=json --app auth.group
py .\manage.py loaddata .\mockup\mockup_users\MOCK_DATA_USER.json --format=json --app auth.user
py .\manage.py loaddata .\mockup\mockup_contest\MOCK_DATA_CONTEST.json --format=json --app api.contest
py .\manage.py loaddata .\mockup\mockup_photos\MOCK_DATA_PHOTO.json --format=json --app api.photo
py .\manage.py loaddata .\mockup\mockup_votes\MOCK_DATA_VOTES.json --format=json --app api.vote

python .\manage.py runserver 0.0.0.0:8000