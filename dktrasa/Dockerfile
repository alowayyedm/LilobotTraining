FROM rasa/rasa:3.6.20-full
WORKDIR '/app'
COPY . /app
USER root
RUN pip install rasa[spacy] && python -m spacy download nl_core_news_lg
USER 1001
CMD ["run","-m","/app/models","--enable-api","--cors","*", "-t","test"]
