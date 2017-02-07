// requires ==========================================================================

var express = require('express');
var fileUpload = require('express-fileupload');
var http = require('http');
var fs = require('fs');
var sqlite3 = require('sqlite3').verbose();
var app = express();
var io = require('socket.io')(http.Server(app));

// global functions ==================================================================

function errorResponse(res){
    return function(message){
        response = JSON.stringify({success: false, errorMessage: message});
        console.log(response);
        res.send(response);
    }
}

function successResponse(res){
    return function(object){
        object.success = true;
        object.errorMessage = '';
        response = JSON.stringify(object);
        res.send(response);
    }
}

// events & routes ===================================================================

io.on('connection', function(){ /* … */ });

app.use('/public', express.static('public'));
app.use(fileUpload());
app.engine('html', require('ejs').renderFile);

app.get('/', function (req, res) {
    runDb(null, function(db){
        userRows = [];
        matchRows = [];
        chatRows = [];
        db.all("SELECT * FROM user", function(err, rows){
            userRows = rows;
            db.all("SELECT * FROM match", function(err, rows){
                matchRows = rows;
                db.all("SELECT * FROM chat", function(err, rows){
                    chatRows = rows;
                    res.render(__dirname+'/views/index.html', {"userRows":userRows,"chatRows":chatRows,"matchRows":matchRows});
                });
            });
        });
    }, function(){});
});

app.get('/register', function (req, res){
    email = req.query.email;
    name = req.query.name;
    password = req.query.password;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    register(null, email, name, password, callbackSuccess, errorResponse(res));
});

app.get('/loginBySession', function(req, res){
    session = req.query.session;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    loginBySession(null, session, callbackSuccess, errorResponse(res));
});

app.get('/loginByEmail', function(req, res){
    email = req.query.email;
    password = req.query.password;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    loginByEmail(null, email, password, callbackSuccess, errorResponse(res));
});

app.get('/move', function(req, res){
    session = req.query.session;
    newLat = req.query.newLat;
    newLon = req.query.newLon;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    move(null, session, newLat, newLon, callbackSuccess, errorResponse(res));
});

app.post('/changeProfilePicture', function(req, res){
    console.log(req);
    session = req.body.session;
    file = req.files.profilePicture;
    file.mv(__dirname+'/public/uploads/'+file.name, function(err){
        // update database
        callbackSuccess = function(db, userRow){
            object = {session:userRow.session};
            successResponse(res)(object);
        }
        changeProfilePicture(null, session, file.name, callbackSuccess, errorResponse(res));
    });
});

app.get('/swipeRight', function(req, res){
    session = req.query.session;
    targetEmail = req.query.targetEmail;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    swipeRight(null, session, targetEmail, callbackSuccess, errorResponse(res));
});

app.get('/chat', function(req, res){
    session = req.query.session;
    targetEmail = req.query.targetEmail;
    message = req.query.message;
    callbackSuccess = function(db, userRow){
        object = {session:userRow.session};
        successResponse(res)(object);
    }
    chat(null, session, targetEmail, message, callbackSuccess, errorResponse(res));
});

app.get('/getAvailableUserList', function(req, res){
    session = req.query.session;
    radius = req.query.radius;
    callbackSuccess = function(db, userRow, availableUserList){
        object = {session:userRow.session, userList:availableUserList};
        successResponse(res)(object);
    }
    getAvailableUserList(null, session, radius, callbackSuccess, errorResponse(res));
});

app.get('/getMatchList', function(req, res){
    session = req.query.session;
    callbackSuccess = function(db, userRow, matchList){
        object = {session:userRow.session, userList:matchList};
        successResponse(res)(object);
    }
    getMatchList(null, session, callbackSuccess, errorResponse(res));
});

app.get('/getChatList', function(req, res){
    session = req.query.session;
    targetEmail = req.query.targetEmail;
    callbackSuccess = function(db, userRow, list){
        object = {session:userRow.session, chatList:list};
        successResponse(res)(object);
    }
    getChatList(null, session, targetEmail, callbackSuccess, errorResponse(res));
});

app.get('/test', function(req, res){
    var message = '';
    // flush output
    flushOutput = function(){
        res.send(message);
    }
    addOutput = function(output){
        console.log(output);
        message += output + '<br />';
    }
    // callback error
    callbackError = function(errorMessage){
        addOutput(errorMessage);
        flushOutput();
    };
    callbackShirouChatList = function(db, shirou, chatList){
        if(chatList.length == 1 && chatList[0].email_from == 'shirou@test.com' && chatList[0].email_to == 'rin@test.com' && chatList[0].message == 'Hello Rin'){
            addOutput('SHIROU CHATLIST SUCCESS');
            // end of test
            flushOutput();
        }
        else{
            callbackError('SHIROU CHATLIST FAILED');
        }
    }
    callbackShirouChat = function(db, shirou){
        db.all("SELECT * FROM chat", function(err, rows){
            if(rows.length == 1 && rows[0].email_from == 'shirou@test.com' && rows[0].email_to == 'rin@test.com' && rows[0].message == 'Hello Rin'){
                addOutput('SHIROU CHAT RIN SUCCESS');
                // next test
                getChatList(db, shirou.session, 'rin@test.com', callbackShirouChatList, callbackError);
            }
            else{
                callbackError('SHIROU CHAT RIN FAILED');
            }
        });
    }
    callbackShirouMatchList = function(db, shirou, matchList){
        if(matchList.length == 1 && matchList[0].name == 'rin'){
            addOutput('SHIROU MATCH LIST SUCCESS');
            // next test
            chat(db, shirou.session, 'rin@test.com', 'Hello Rin', callbackShirouChat, callbackError);
        }
        else{
            callbackError('SHIROU MATCH LIST FAILED');
        }
    }
    callbackShirouSwipeRightRin = function(db, shirou){
        db.all("SELECT * FROM match WHERE email_from='shirou@test.com' AND email_to='rin@test.com'", function(err, rows){
            if(rows.length == 1){
                addOutput('SHIROU SWIPE RIGHT RIN SUCCESS');
                // next test
                getMatchList(db, shirou.session, callbackShirouMatchList, callbackError);
            }
            else{
                callbackError('SHIROU SWIPE RIGHT FAILED');
            }
        });
    }
    callbackShirouAvailableUserList = function(db, shirou, availableUserList){
        if(availableUserList.length == 2 && ((availableUserList[0].name == 'rin' && availableUserList[1].name == 'ilya') || (availableUserList[1].name == 'rin' && availableUserList[0].name == 'ilya'))){
            addOutput('SHIROU GET AVAILABLE USERS SUCCESS');
            // next test
            swipeRight(db, shirou.session, 'rin@test.com', callbackShirouSwipeRightRin, callbackError);
        }
        else{
            callbackError('SHIROU GET AVAILABLE USERS FAILED');
        }
    }
    callbackMoveShirou = function(db, shirou){
        if(shirou.lat == 7 && shirou.lon == 8){
            addOutput('MOVE SHIROU TO 7,8 SUCCESS');
            // cheat: prepare rin, ilya, and gilgamesh
            db.run("UPDATE user SET lat=7, lon=8.0000001 WHERE email='rin@test.com'");
            db.run("UPDATE user SET lat=7, lon=8.0000002 WHERE email='ilya@test.com'");
            db.run("UPDATE user SET lat=6, lon=9.0000002 WHERE email='gilgamesh@test.com'");
            db.run("INSERT INTO match(email_from, email_to) VALUES('rin@test.com', 'shirou@test.com')");
            db.run("INSERT INTO match(email_from, email_to) VALUES('ilya@test.com', 'shirou@test.com')");
            // next test
            getAvailableUserList(db, shirou.session, 10, callbackShirouAvailableUserList, callbackError);
        }
        else{
            callbackError('MOVE SHIROU TO 7,8 FAILED');
        }
    }
    callbackLoginShirouBySession = function(db, shirou){
        if(shirou.email == 'shirou@test.com' && shirou.name == 'shirou' && shirou.password == 'shiroupassword' && shirou.session != ''){
            addOutput('LOGIN SHIROU BY SESSION SUCCESS');
            move(db, shirou.session, 7, 8, callbackMoveShirou, callbackError);
        }
        else{
            callbackError('LOGIN SHIROU BY SESSION FAILED');
        }
    }
    // callback login shirou by email
    callbackLoginShirouByEmail = function(db, shirou){
        if(shirou.email == 'shirou@test.com' && shirou.name == 'shirou' && shirou.password == 'shiroupassword' && shirou.session != ''){
            addOutput('LOGIN SHIROU BY EMAIL SUCCESS');
            loginBySession(db, shirou.session, callbackLoginShirouBySession, callbackError);
        }
        else{
            callbackError('LOGIN SHIROU BY EMAIL FAILED');
        }
    }
    // callback register Shirou 
    callbackRegisterShirou = function(db, shirou){
        if(shirou.email == 'shirou@test.com' && shirou.name == 'shirou' && shirou.password == 'shiroupassword'){
            addOutput('REGISTER SHIROU SUCCESS');
            loginByEmail(db, 'shirou@test.com', 'shiroupassword', callbackLoginShirouByEmail, callbackError);
        }
        else{
            callbackError('REGISTER SHIROU FAILED');
        }
    }
    // callback register Gilgamesh 
    callbackRegisterGilgamesh = function(db, gilgamesh){
        if(gilgamesh.email == 'gilgamesh@test.com' && gilgamesh.name == 'gilgamesh' && gilgamesh.password == 'gilgameshpassword'){
            addOutput('REGISTER GILGAMESH SUCCESS');
            register(db, 'shirou@test.com', 'shirou', 'shiroupassword', callbackRegisterShirou, callbackError);
        }
        else{
            callbackError('REGISTER GILGAMESH FAILED');
        }
    }
    // callback register Ilya 
    callbackRegisterIlya = function(db, ilya){
        if(ilya.email == 'ilya@test.com' && ilya.name == 'ilya' && ilya.password == 'ilyapassword'){
            addOutput('REGISTER ILYA SUCCESS');
            register(db, 'gilgamesh@test.com', 'gilgamesh', 'gilgameshpassword', callbackRegisterGilgamesh, callbackError);
        }
        else{
            callbackError('REGISTER ILYA FAILED');
        }
    }
    // callback register Rin 
    callbackRegisterRin = function(db, rin){
        if(rin.email == 'rin@test.com' && rin.name == 'rin' && rin.password == 'rinpassword'){
            addOutput('REGISTER RIN SUCCESS');
            register(db, 'ilya@test.com', 'ilya', 'ilyapassword', callbackRegisterIlya, callbackError);
        }
        else{
            callbackError('REGISTER RIN FAILED');
        }
    }
    // test
    test = function(db){
        // test register rin
        register(db, 'rin@test.com', 'rin', 'rinpassword', callbackRegisterRin, callbackError);
    }
    // real job
    var db = new sqlite3.Database(":memory:");
    runDb(db, test, callbackError);
});

app.set('port', (process.env.PORT || 5000));
app.listen(app.get('port'), function () {
    console.log('Finder web service run at port ' + app.get('port'));
});


// functions =========================================================================

/**
 * Calculate distance between two points in meters
 * @param {Number} lat1
 * @param {Number} lon1
 * @param {Number} lat2
 * @param {Number} lon2
 * @return {Number}
 */
function distance(lat1, lon1, lat2, lon2){
    var R = 6371e3; // meters
    var phi1 = lat1 * (22.0/7.0)/180;
    var phi2 = lat2 * (22.0/7.0)/180;
    var deltaphi = (lat2-lat1) * (22.0/7.0)/180;
    var deltalambda = (lon2-lon1) * (22.0/7.0)/180;

    var a = Math.sin(deltaphi/2) * Math.sin(deltaphi/2) +
        Math.cos(phi1) * Math.cos(phi2) *
        Math.sin(deltalambda/2) * Math.sin(deltalambda/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    var d = R * c;
    return d;
}

function addZeroPrefix(str, digitCount){
    while(str.length < digitCount){
        str = '0' + str;
    }
    return str;
}

/**
 * Turn date into formatted string
 * @param {Date} date
 * @return {String}
 */
function dateToStr(date, format) {
    Y = addZeroPrefix(date.getFullYear().toString(), 4);
    M = addZeroPrefix((date.getMonth() + 1).toString(), 2);
    D = addZeroPrefix(date.getDate().toString(), 2);
    h = addZeroPrefix(date.getHours().toString(), 2);
    m = addZeroPrefix(date.getMinutes().toString(), 2);
    s = addZeroPrefix(date.getSeconds().toString(), 2);
    return Y + '-' + M + '-' + D + ' ' + h + ':' + m + ':' + s;
}

/**
 * Run DB operation 
 * @param {Object} db
 * @param {Function} callback(db)
 */
function runDb(db, callback){
    // prepare db
    if(db == null){
        file = __dirname+'/finder.db';
        db = new sqlite3.Database(file);
    }
    // do database action
    db.serialize(function(){
        // create table if not exists
        db.run("CREATE TABLE IF NOT EXISTS user(email TEXT, name TEXT, password TEXT, session TEXT, lon DOUBLE, lat DOUBLE, profile_picture TEXT)");
        db.run("CREATE TABLE IF NOT EXISTS match(email_from TEXT, email_to TEXT)");
        db.run("CREATE TABLE IF NOT EXISTS chat(email_from TEXT, email_to TEXT, message TEXT, time TEXT)");
        // run callback
        callback(db);
    });
}

/**
 * Get user by session or email, run callbacks on condition
 * @param {Object} db
 * @param {String} identity
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function getUser(db, identity, callbackSuccess, callbackError){
    callbackRunDb = function(db){
        var sql = "SELECT * FROM user WHERE session = ? OR email = ?";
        var params = [identity, identity];
        db.all(sql, params, function(err, rows){
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }else if(rows.length > 0 && typeof callbackSuccess === 'function')
            {
                callbackSuccess(db, rows[0]);
            }
            else if(typeof callbackError === 'function'){
                callbackError('User not found');
            }
        });
    }
    runDb(db, callbackRunDb);
}

/**
 * Update user session based on email or old session run callbacks based on condition 
 * @param {Object} db
 * @param {String} identity
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function updateSession(db, identity, callbackSuccess, callbackError){
    callbackGetUser = function (db, userRow){
        var session = userRow.email + '_' + Math.round(Math.random()*10000);
        var sql = "UPDATE user SET session = ? WHERE email = ? OR session = ?";
        var params = [session, identity, identity];
        db.run(sql, params);
        userRow.session = session;
        if(typeof callbackSuccess == 'function'){
            callbackSuccess(db, userRow);
        }
    }
    getUser(db, identity, callbackGetUser, callbackError)
}

/**
 * Register new user 
 * @param {Object} db
 * @param {String} email
 * @param {String} name
 * @param {String} password
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function register(db, email, name, password, callbackSuccess, callbackError){
    callbackRunDb = function(db){
        var sql = "SELECT * FROM user WHERE email = ?"; 
        var params = [email];
        db.all(sql, params, function(err, rows){
            if(rows.length == 0){
                sql = "INSERT INTO user(email, name, password, lat, lon) VALUES(?,?,?, -1, -1)";
                params = [email, name, password];
                db.run(sql, params);
                // also update session
                updateSession(db, email, callbackSuccess, callbackError);
            }
            else if(typeof callbackError === 'function'){
                callbackError('User already exists');
            }
        });
    }
    runDb(db, callbackRunDb);
}

/**
 * Login by session 
 * @param {Object} db
 * @param {String} session
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function loginBySession(db, session, callbackSuccess, callbackError){
    updateSession(db, session, callbackSuccess, callbackError);
}


/**
 * Login by email & password 
 * @param {Object} db
 * @param {String} session
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function loginByEmail(db, email, password, callbackSuccess, callbackError){
    callbackRunDb = function(db){
        var sql = "SELECT * FROM user WHERE email = ? AND password = ?"; 
        var params = [email, password];
        db.all(sql, params, function(err, rows){
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }else if(rows.length > 0){
                updateSession(db, email, callbackSuccess, callbackError);
            }
            else if(typeof callbackError === 'function'){
                callbackError('Invalid email/password');
            }
        });
    }
    runDb(db, callbackRunDb);
}

/**
 * Move
 * @param {Object} db
 * @param {Object} session
 * @param {Number} newLat
 * @param {Number} newLon
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function move(db, session, newLat, newLon, callbackSuccess, callbackError){
    callbackLoginBySession = function(db, currentUser){
        var sql = "UPDATE user SET lat=?, lon=? WHERE email=?";
        var params = [newLat, newLon, currentUser.email];
        db.run(sql, params);
        if(typeof callbackSuccess == 'function'){
            getUser(db, currentUser.session, callbackSuccess, callbackError);
        }
    }
    loginBySession(db, session, callbackLoginBySession, callbackError);
}

/**
 * changeProfilePicture
 * @param {Object} db
 * @param {Object} session
 * @param {Object} file
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function changeProfilePicture(db, session, fileName, callbackSuccess, callbackError){
    console.log(session);
    callbackLoginBySession = function(db, currentUser){
        var sql = "UPDATE user SET profile_picture=? WHERE email=?";
        var params = [fileName, currentUser.email];
        db.run(sql, params);
        if(typeof callbackSuccess == 'function'){
            getUser(db, currentUser.session, callbackSuccess, callbackError);
        }
    }
    loginBySession(db, session, callbackLoginBySession, callbackError);
}

/**
 * Swipe right (add to match table)
 * @param {Object} db
 * @param {String} session
 * @param {String} targetEmail
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function swipeRight(db, session, targetEmail, callbackSuccess, callbackError){
    callbackLoginBySession = function(db, currentUser){
        var sql = "SELECT * FROM match WHERE email_from=? AND email_to = ?";
        var params = [currentUser.email, targetEmail];
        db.all(sql, params, function(err, rows){
            // add to match table if not exists
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }
            else if(rows.length == 0){
                sql = "INSERT INTO match(email_from, email_to) VALUES(?,?)";
                params = [currentUser.email, targetEmail];
                db.run(sql, params);
                if(typeof callbackSuccess === 'function'){
                    callbackSuccess(db, currentUser);
                }
            }
            else if(typeof callbackError === 'function'){
                callbackError("Already swiped right");
            }
        });
    }
    loginBySession(db, session, callbackLoginBySession, callbackError);
}

/**
 * Chat 
 * @param {Object} db
 * @param {String} session
 * @param {String} targetEmail
 * @param {String} email
 * @param {Function} callbackSuccess(db, userRow)
 * @param {Function} callbackError(errorMessage)
 */
function chat(db, session, targetEmail, message, callbackSuccess, callbackError){
    callbackMatchList = function(db, currentUser, matchList){
        var chatAllowed = false;
        for(i=0; i<matchList.length; i++){
            match = matchList[i];
            if(match.email == targetEmail){
                chatAllowed = true;
                break;
            }
        }
        if(chatAllowed){
            var sql = "INSERT INTO chat(email_from, email_to, message, time) VALUES(?,?,?,?)";
            var params = [currentUser.email, targetEmail, message, dateToStr(new Date(Date.now()))];
            db.run(sql, params);
            // push notification to clients
            io.emit('chat-to-'+targetEmail, message);
            if(typeof callbackSuccess === 'function'){
                callbackSuccess(db, currentUser);
            }
        }else{
            callbackError('Cannot chat the user');
        }
    }
    getMatchList(db, session, callbackMatchList, callbackError);
}

/**
 * Chat 
 * @param {Object} db
 * @param {String} session
 * @param {Number} radius
 * @param {Function} callbackSuccess(db, userRow, userList)
 * @param {Function} callbackError(errorMessage)
 */
function getAvailableUserList(db, session, radius, callbackSuccess, callbackError){
    callbackMatchList = function(db, currentUser, matchList){
        var sql = "SELECT * FROM user WHERE email != ? ORDER BY RANDOM()";
        var params = [currentUser.email];
        db.all(sql, params, function(err, rows){
            availableUserList = []
            for(i=0; i<rows.length; i++){
                row = rows[i];
                // only get users that are note swiped right
                var alreadyMatched = false;
                for(j=0; j<matchList.length; j++){
                    match = matchList[j];
                    if(match.email == row.email){
                        alreadyMatched = true;
                        break;
                    }
                }
                if(!alreadyMatched && distance(row.lat, row.lon, currentUser.lat, currentUser.lon) <= radius*1000){
                    availableUserList.push(row);
                }
            }
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }else if(typeof callbackSuccess === 'function'){
                callbackSuccess(db, currentUser, availableUserList);
            }
        });
    }
    getMatchList(db, session, callbackMatchList, callbackError);
}

/**
 * Match List 
 * @param {Object} db
 * @param {String} session
 * @param {Function} callbackSuccess(db, userRow, matchList)
 * @param {Function} callbackError(errorMessage)
 */
function getMatchList(db, session, callbackSuccess, callbackError){
    callbackLoginBySession = function(db, currentUser){
        var sql = "SELECT * FROM user WHERE email != ? AND (SELECT COUNT(*) FROM match WHERE email_from = ? AND email_to = user.email) > 0 AND (SELECT COUNT(*) FROM match WHERE email_to = ? AND email_from = user.email) > 0";
        var params = [currentUser.email, currentUser.email, currentUser.email];
        db.all(sql, params, function(err, rows){
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }else if(typeof callbackSuccess === 'function'){
                callbackSuccess(db, currentUser, rows);
            }
        });
    }
    loginBySession(db, session, callbackLoginBySession, callbackError);
}

/**
 * Chat List 
 * @param {Object} db
 * @param {String} session
 * @param {String} targetEmail
 * @param {Function} callbackSuccess(db, userRow, chatList)
 * @param {Function} callbackError(errorMessage)
 */
function getChatList(db, session, targetEmail, callbackSuccess, callbackError){
    callbackLoginBySession = function(db, currentUser){
        var sql = "SELECT * FROM chat WHERE (email_from=? AND email_to=?) OR (email_from=? AND email_to=?) ORDER BY time";
        var params = [currentUser.email, targetEmail, targetEmail, currentUser.email];
        db.all(sql, params, function(err, rows){
            if(err && typeof callbackError === 'function'){
                callbackError(err.message);
            }else if(typeof callbackSuccess === 'function'){
                callbackSuccess(db, currentUser, rows);
            }
            else if(typeof callbackError === 'function'){
                callbackError('Failed to get chat list');
            }
        });
    }
    loginBySession(db, session, callbackLoginBySession, callbackError);
}
