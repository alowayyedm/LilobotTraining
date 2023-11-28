FROM rasa/rasa-sdk:3.5.1
WORKDIR /app
COPY ./ /app/actions
COPY requirements.txt requirements.txt
USER root
RUN python -m pip install --upgrade pip
RUN pip install --verbose -r requirements.txt
EXPOSE 5055
USER 1001
