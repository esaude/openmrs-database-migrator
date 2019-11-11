#!/bin/bash
# This short script runs the database restore with given arguments.
##where :
#"$1" is for username
#"$2" is for password
#"$3" is for database name
#"$4" is for mysql form data dump

mysql -u "$1" -p"$2" "$3" < "$4"
