# cognitive-exercises-memory

This microservice contains classic memory game. It uses mongodb for data storage.

## Running in docker 

Docker compose file is not to be run on its own. It should be copied into docker compose file that runs all the services. 

After running "docker compose up" one needs to enter backend centos container by using
"docker exec -it {container_name} bash" and run the cognitive-exercises-memory app by using one of the scripts:
	- run-cog-ex-memory-bootstrap.sh (to put sample data into db)
	- run-cog-ex-memory.sh (running after sample data is put)