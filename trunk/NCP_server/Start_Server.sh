#!/bin/bash
#Start_Server.sh
#NCP_Team
function start
{
	java -jar NCP_Server.jar
	checkErrLv
}

function checkErrLv
{
	if [$? = 5]
	then
		start
	else
		end
}

function end
{
	sleep
}