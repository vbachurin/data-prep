FROM ubuntu

# update and install all required packages (no sudo required as root)
# https://gist.github.com/isaacs/579814#file-only-git-all-the-way-sh
RUN apt-get update -yq && apt-get upgrade -yq && \
		apt-get install -yq curl git nano

# install from nodesource using apt-get
# https://www.digitalocean.com/community/tutorials/how-to-install-node-js-on-an-ubuntu-14-04-server
RUN curl -sL https://deb.nodesource.com/setup | sudo bash - && \
		apt-get install -yq nodejs build-essential

# fix npm - not the latest version installed by apt-get
RUN npm install -g npm

# add user with sudo privileges within Docker container 
# without adduser input questions
# http://askubuntu.com/questions/94060/run-adduser-non-interactively/94067#94067
ENV USER="testuser"
RUN adduser --disabled-password --gecos "" $USER && \
		sudo usermod -a -G sudo $USER && \
		echo "$USER:abc123" | chpasswd && \
		su - $USER # switch to testuser

# install common full-stack JavaScript packages globally
# http://blog.nodejs.org/2011/03/23/npm-1-0-global-vs-local-installation
RUN sudo npm install -g bower gulp

# optional, check locations and packages are correct
which node; node -v; which npm; npm -v; \
RUN npm ls -g --depth=0
