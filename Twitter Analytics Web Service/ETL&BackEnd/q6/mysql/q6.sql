CREATE TABLE tweets_q6 (
  user_id int unsigned,
  photo int unsigned,
  photo_total int unsigned,
  PRIMARY KEY (user_id)
);

load data infile 'q6_data' into table tweets_q6 fields terminated by '\t' lines terminated by '\n';