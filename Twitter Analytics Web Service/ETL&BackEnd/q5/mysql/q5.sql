CREATE TABLE tweets_q5 (
  user_id int unsigned,
  score_1 int unsigned,
  score_2 int unsigned,
  score_3 int unsigned,
  total_score int unsigned,
  PRIMARY KEY (user_id)
);

load data infile 'q5_data' into table tweets_q5 fields terminated by '\t' lines terminated by '\n';