CREATE TABLE user(
id INTEGER AUTO_INCREMENT PRIMARY KEY,
compte varchar(50) NOT NULL,
mdp varchar(50) NOT NULL,
mail varchar(150) NOT NULL,
lvAccess INTEGER NOT NULL DEFAULT 0, -- -1 banni,0 normal,1 moderateur,2 admin 
ip varchar(15) NOT NULL,
lastConnection varchar(10) NOT NULL;

CREATE TABLE ban(
ip varchar(15) NOT NULL,
finBan timestamp) NOT NULL;


