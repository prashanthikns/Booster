from app import app
import pg
from flask import Flask, request, flash, url_for, redirect, \
     render_template, abort
import json

db=pg.connect('mydb1', 'ec2-54-70-148-118.us-west-2.compute.amazonaws.com', 5432, None,  'test1', 'test1')

@app.route('/')

@app.route('/map/<queryname>/<param>')
def map(queryname, param):
        return render_template('map.html', title = 'Booster', queryname = queryname,
                               param = param)

@app.route('/polygons')
def send():
    return "<a href=%s>file</a>" % url_for('static', filename='service.json')

@app.route('/trucks/<truckname>')
def trucks(truckname):
    table_name = 'deviceTimeTable'

    if truckname == "all" :
	result = db.query('SELECT deviceId, deviceName, latitude, longitude FROM ' + table_name +
		" ORDER BY latesttimestamp desc;")
    else :
    	result = db.query('SELECT deviceId, deviceName, latitude, longitude FROM ' + table_name +
		" WHERE deviceName = '"+ truckname +  "';")

    #Now turn the results into valid JSON
    return str(json.dumps(list(result.namedresult())))


@app.route('/fuel/<lat>/<lon>')
def fuel(lat, lon):
	table_device_name = 'deviceTimeTable'

	result = db.query('SELECT deviceId, deviceName, latitude, longitude FROM ' + table_device_name +
                          ' WHERE (ST_MaxDistance('+ "'" + 'POINT('+ lat + ' ' + lon +')' + "'" +
                 	  '::geometry, ST_MakePoint(latitude, longitude)) < 25);')

        return str(json.dumps(list(result.namedresult())))


