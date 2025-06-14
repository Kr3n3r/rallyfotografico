<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![project_license][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Kr3n3r/rallyfotografico">
    <img src=".github/images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Rally Fotográfico</h3>

  <p align="center">
    An mobile project to simulate photo rally
    <br />
    <a href="https://github.com/Kr3n3r/rallyfotografico"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Kr3n3r/rallyfotografico">View Demo</a>
    &middot;
    <a href="https://github.com/Kr3n3r/rallyfotografico/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/Kr3n3r/rallyfotografico/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<img src=".github/images/screenshot.png" alt="Logo">

This project includes photo rally app built for TFG module of DAM at I.E.S Velázquez

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Next][Next.js]][Next-url]
* [![React][React.js]][React-url]
* [![Vue][Vue.js]][Vue-url]
* [![Angular][Angular.io]][Angular-url]
* [![Svelte][Svelte.dev]][Svelte-url]
* [![Laravel][Laravel.com]][Laravel-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

* python >= 3.11
  ```sh
  https://www.python.org/downloads/
  ```
* Android Device or emulator and IDE

### Installation

1. Create a folder and
2. Clone the repo
   ```sh
   git clone https://github.com/Kr3n3r/rallyfotografico.git .
   ```
3. Create python virtual env and install dependencies
   ```sh
    py -m venv .venv
    .\.venv\Scripts\Activate.ps1
    pip install -r .\requirements.txt
    cd .\restapi\
   ```
4. Apply migrations
   ```sh
   python .\manage.py migrate
   ```
5. (OPTIONAL) Launch the mockup
   ```sh
   py .\manage.py loaddata .\mockup\mockup_groups\MOCK_DATA_GROUPS.json --format=json --app auth.group
   py .\manage.py loaddata .\mockup\mockup_users\MOCK_DATA_USER.json --format=json --app auth.user
   py .\manage.py loaddata .\mockup\mockup_contest\MOCK_DATA_CONTEST.json --format=json --app api.contest
   py .\manage.py loaddata .\mockup\mockup_photos\MOCK_DATA_PHOTO.json --format=json --app api.photo
   py .\manage.py loaddata .\mockup\mockup_votes\MOCK_DATA_VOTES.json --format=json --app api.vote
   ```
6. Run a server
   ```sh
   python .\manage.py runserver 0.0.0.0:8000
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

See user, admin and installation manual.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [ ] Feature 1
- [ ] Feature 2
- [ ] Feature 3
    - [ ] Nested Feature

See the [open issues](https://github.com/Kr3n3r/rallyfotografico/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/Kr3n3r/rallyfotografico/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Kr3n3r/rallyfotografico" alt="contrib.rocks image" />
</a>



<!-- LICENSE -->
## License

Distributed under the project_license. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - marmolromeroalejandro@gmail.com

Project Link: [https://github.com/Kr3n3r/rallyfotografico](https://github.com/Kr3n3r/rallyfotografico)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Kr3n3r/rallyfotografico.svg?style=for-the-badge
[contributors-url]: https://github.com/Kr3n3r/rallyfotografico/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Kr3n3r/rallyfotografico.svg?style=for-the-badge
[forks-url]: https://github.com/Kr3n3r/rallyfotografico/network/members
[stars-shield]: https://img.shields.io/github/stars/Kr3n3r/rallyfotografico.svg?style=for-the-badge
[stars-url]: https://github.com/Kr3n3r/rallyfotografico/stargazers
[issues-shield]: https://img.shields.io/github/issues/Kr3n3r/rallyfotografico.svg?style=for-the-badge
[issues-url]: https://github.com/Kr3n3r/rallyfotografico/issues
[license-shield]: https://img.shields.io/github/license/Kr3n3r/rallyfotografico.svg?style=for-the-badge
[license-url]: https://github.com/Kr3n3r/rallyfotografico/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: images/screenshot.png
[Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com 