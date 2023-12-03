// Copyright 20-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// Cgi communications
public class Cgi {
  /** Keys length */
  public static final int KLEN = 300;

  static final long T_NO_EXPIRATION = 2592000; // seconds == 30 days
  static final String DEME_KEY =
    "nkXliX8lg2kTuQSS/OoLXCk8eS4Fwmc+N7l6TTNgzM1vdKewO0cjok51vcdl" +
    "OKVXyPu83xYhX6mDeDyzapxL3dIZuzwyemVw+uCNCZ01WDw82oninzp88Hef" +
    "bn3pPnSMqEaP2bOdX+8yEe6sGkc3IO3e38+CqSOyDBxHCqfrZT2Sqn6SHWhR" +
    "KqpJp4K96QqtVjmXwhVcST9l+u1XUPL6K9HQfEEGMGcToMGUrzNQxCzlg2g+" +
    "Hg55i7iiKbA0ogENhEIFjMG+wmFDNzgjvDnNYOaPTQ7l4C8aaPsEfl3sugiw"
  ;
  static final String USERS_DB = "users.db";
  static final String SESSIONS_DB = "sessions.db";

  // Key to encrypt files
  static String fkey = Cryp.key(DEME_KEY, DEME_KEY.length());

  // Key to encrypt cummunications
  Optional<String> key = Optional.empty();
  long tExpiration = T_NO_EXPIRATION;

  /**
   * Constructor.
   * @param tExpiration Expiration time.
   */
  public Cgi(long tExpiration) {
    this.tExpiration = tExpiration;

    if (!Std.fexists(usersPath())) {
      ArrayList<User> us = new ArrayList<>();
      writeUsers(us);
      putUser(new User("admin", Cryp.key(DEME_KEY, KLEN), "0"));
      ArrayList<Session> ss = new ArrayList<>();
      writeSessions(ss);
    }
  }

  /**
   * Sets commnications key.
   * @param k Key
   */
  public void setKey (String k) {
    key = Optional.of(k);
  }

  /**
   * Process a normal response.
   * Its type is:<pre>
   *    {
   *      "k1": value,
   *      "k2"; value,
   *      ...
   *    }
   * </pre>
   * @param response Response
   * @return A String
   */
  public String ok (Map<String, Js> response) {
    String k = key.orElseThrow(() ->
      new IllegalStateException("Communication key not set")
    );
    key = Optional.empty();
    return Cryp.encode(Js.write(response).toString(), k);
  }

  /**
   * Process an empty response.
   * Its type is:<pre>
   *    {
   *    }
   * </pre>
   * @return A String
   */
  public String empty () {
    return ok(new HashMap<String, Js>());
  }

  /**
   * Process an error response.
   * Its type is:<pre>
   *    {
   *      "error": "msg"
   *    }
   * </pre>
   * @param msg Error message.
   * @return A String
   */
  public String error (String msg) {
    return ok(new HashMap<String, Js>() {{
      put("error", Js.write(msg));
    }});
  }

  /**
   * Process an expired response.
   * Its type is:<pre>
   *    {
   *      "expired": true
   *    }
   * </pre>
   * @return A String
   */
  public String expired () {
    return ok(new HashMap<String, Js>() {{
      put("expired", Js.write(true));
    }});
  }

  /**
   * Returns session data
   * @param sessionId Session identifier.
   * @return Session data. If sessionId is wrong, SessionData has its fields
   *         set to "".
   */
  synchronized public SessionData getSessionData (String sessionId) {
    for (Session s : readSessions()) {
      if (s.id.equals(sessionId)) {
        return new SessionData(s.key, s.connectionId);
      }
    }
    return new SessionData("", "");
  }

  /**
   * Tries a connection.
   * @param sessionId Session Identifier.
   * @return If sessionId is valid returns:<pre>
   *    {
   *      "key": "xxxx",        // Communications key
   *      "connectionId": "xxx" // Connection identifier
   *    }
   * </pre>
   * else returns: <pre>
   *    {
   *      "key": "",
   *      "connectionId": ""
   *    }
   * </pre>
   */
  synchronized public String connect (String sessionId) {
    String k = "";
    String connectionId = "";
    long now = Date.toMillis(Date.tnow());
    List<Session> ss = readSessions().stream()
      .filter(s -> s.expire > now)
      .collect(Collectors.toList());

    for (Session s : ss) {
      if (s.id.equals(sessionId)) {
        k = s.key;
        connectionId = Cryp.genk(KLEN);
        s.connectionId = connectionId;
        s.expire = now + s.lapse;
      }
    }

    writeSessions(ss);

    HashMap<String,Js> m = new HashMap<>();
    m.put("key", Js.write(k));
    m.put("connectionId", Js.write(connectionId));
    return ok(m);
  }


  /**
   * Tries an authentication.
   * @param user User id.
   * @param ukey User password.
   * @param expiration Session expiration time. If its value is 0, expiration
   *        time will be maximum.
   * @return If authentication is valid returns:<pre>
   *    {
   *      "level": "xxxx",    // User level "0" = Administrator
   *      "sessionId": "xxx", // Session identifier
   *      "key": "xxx"        // Communication key
   *    }
   * </pre>
   *  else returns: <pre>
   *    {
   *      "level": "",
   *      "sessionId": "",
   *      "key" = ""
   *    }
   * </pre>
   */
  synchronized public String authentication (
    String user, String ukey, boolean expiration
  ) {
    String sessionId = "";
    String key = "";

    String level = checkUser(user, ukey);
    if (!level.isEmpty()) {
      sessionId = Cryp.genk(KLEN);
      key = Cryp.genk(KLEN);
      long lapse = (expiration ? tExpiration : T_NO_EXPIRATION) * 1000;
      long expire = Date.toMillis(Date.tnow()) + lapse;
      putSession(new Session(sessionId, key, user, "", expire, lapse));
    }

    HashMap<String,Js> m = new HashMap<>();
    m.put("level", Js.write(level));
    m.put("sessionId", Js.write(sessionId));
    m.put("key", Js.write(key));
    return ok(m);
  }

  /**
   * Deletes a session. If session does not exists, it does nothing.
   * @param sessionId Session identifier.
   * @return <pre>
   *    {
   *    }
   * </pre>
   */
  synchronized public String delSession (String sessionId) {
    writeSessions(
      readSessions().stream()
      .filter(s -> !s.id.equals(sessionId))
      .collect(Collectors.toList())
    );
    return empty();
  }

  /**
   * Adds a new user.
   * @param admin Administrator identifier
   * @param akey Administrator password
   * @param user User identifier
   * @param ukey User password
   * @param level User level (0 = Administrator)
   * @return If operation succeds returns:<pre>
   *    {
   *      "ok": true
   *    }
   * </pre>
   * otherwise (Wrong administrator data or user duplicate)<pre>
   *    {
   *      "ok": false
   *    }
   * </pre>
   */
  synchronized public String addUser (
    String admin, String akey, String user, String ukey, String level
  ) {
    String ak = Cryp.key(akey, KLEN);
    String uk = Cryp.key(ukey, KLEN);
    boolean ok = false;
    boolean unique = true;
    boolean adminOk = false;

    for (User u : readUsers()) {
      if (u.id.equals(admin)) {
        if (u.key.equals(ak)) {
          adminOk = true;
        } else {
          break;
        }
      } else if (u.id.equals(user)) {
        unique = false;
        break;
      }
    }

    if (unique && adminOk) {
      putUser(new User(user, uk, level));
      ok = true;
    }

    HashMap<String,Js> m = new HashMap<>();
    m.put("ok", Js.write(ok));
    return ok(m);
  }

  /**
   * Deletes an user.
   * @param admin Administrator identifier
   * @param akey Administrator password
   * @param user User identifier
   * @return If operation succeds returns:<pre>
   *    {
   *      "ok": true
   *    }
   * </pre>
   * otherwise (Wrong administrator data)<pre>
   *    {
   *      "ok": false
   *    }
   * </pre>
   */
  synchronized public String delUser (String admin, String akey, String user) {
    String ak = Cryp.key(akey, KLEN);
    boolean ok = false;
    boolean adminOk = false;

    for (User u : readUsers()) {
      if (u.id.equals(admin)) {
        if (u.key.equals(ak)) {
          adminOk = true;
        }
        break;
      }
    }

    if (adminOk) {
      writeUsers(
        readUsers().stream()
        .filter(u -> !u.id.equals(user))
        .collect(Collectors.toList())
      );
      ok = true;
    }

    HashMap<String,Js> m = new HashMap<>();
    m.put("ok", Js.write(ok));
    return ok(m);

  }

  /**
   * Changes user level.
   * @param admin Administrator identifier
   * @param akey Administrator password
   * @param user User identifier
   * @param level User level
   * @return If operation succeds returns:<pre>
   *    {
   *      "ok": true
   *    }
   * </pre>
   * otherwise (Wrong administrator data or missing user)<pre>
   *    {
   *      "ok": false
   *    }
   * </pre>
   */
  synchronized public String changeUserLevel (
    String admin, String akey, String user, String level
  ) {
    String ak = Cryp.key(akey, KLEN);
    boolean ok = false;
    boolean adminOk = false;
    boolean uexists = false;

    for (User u : readUsers()) {
      if (u.id.equals(admin)) {
        if (u.key.equals(ak)) {
          adminOk = true;
        } else {
          break;
        }
      } else if (u.id.equals(user)) {
        uexists = true;
      }
    }

    if (adminOk && uexists) {
      writeUsers(
        readUsers().stream()
        .map(u -> {
          if (u.id.equals(user)) {
            u.level = level;
          }
          return u;
        })
        .collect(Collectors.toList())
      );
      ok = true;
    }

    HashMap<String,Js> m = new HashMap<>();
    m.put("ok", Js.write(ok));
    return ok(m);
  }

  /**
   * Changes user password.
   * @param user User identifier
   * @param key User old password
   * @param newKey User new password
   * @return If operation succeds returns:<pre>
   *    {
   *      "ok": true
   *    }
   * </pre>
   * otherwise (Wrong user data)<pre>
   *    {
   *      "ok": false
   *    }
   * </pre>
   */
  synchronized public String changeUserPass (
    String user, String key, String newKey
  ) {
    String k = Cryp.key(key, KLEN);
    String nk = Cryp.key(newKey, KLEN);
    boolean ok = false;
    boolean userOk = false;

    for (User u : readUsers()) {
      if (u.id.equals(user)) {
        if (u.key.equals(k)) {
          userOk = true;
        }
        break;
      }
    }

    if (userOk) {
      writeUsers(
        readUsers().stream()
        .map(u -> {
          if (u.id.equals(user)) {
            u.key = nk;
          }
          return u;
        })
        .collect(Collectors.toList())
      );
      ok = true;
    }

    HashMap<String,Js> m = new HashMap<>();
    m.put("ok", Js.write(ok));
    return ok(m);
  }

  // Static -------------------------------------------------------------------

  /** Wrapper for session data */
  public static class SessionData {
    String key;
    String connectionId;

    SessionData (String key, String connectionId) {
      this.key = key;
      this.connectionId = connectionId;
    }

    /**
     * Returns communication key
     * @return Communication key
     */
    public String getKey () {
      return key;
    }

    /**
     * Returns connection identifier.
     * @return Connection identifier.
     */
    public String getConnectionId () {
      return connectionId;
    }
  }

  static String read (String path) {
    return Cryp.decode(Std.read(path), fkey);
  }

  static void write (String path, String tx) {
    Std.write(path, Cryp.encode(tx, fkey));
  }

  static class User { // ------------------------------------------------------
    String id;
    String key;
    String level;

    User (String id, String key, String level) {
      this.id = id;
      this.key = key;
      this.level = level;
    }

    Js toJs() {
      return Js.write(new ArrayList<Js>() {{
        add(Js.write(id));
        add(Js.write(key));
        add(Js.write(level));
      }});
    }

    static User ofJs (Js js) {
      ArrayList<Js> a = js.rArray();
      return new User(
        a.get(0).rString(),
        a.get(1).rString(),
        a.get(2).rString()
      );
    }
  }

  static String usersPath () {
    return Std.fpath(Std.home(), USERS_DB);
  }

  static void writeUsers(List<User> us) {
    Js js = Js.write(
      us.stream().map(u -> u.toJs()).collect(Collectors.toList())
    );
    write(usersPath(), js.toString());
  }

  static List<User> readUsers () {
    return new Js(read(usersPath())).rArray()
      .stream()
      .map(ujs -> User.ofJs(ujs))
      .collect(Collectors.toList())
    ;
  }

  static void putUser(User u) {
    List<User> us = readUsers();
    us.add(u);
    writeUsers(us);
  }

  // If fails return ""
  static String checkUser(String id, String key) {
    String k = Cryp.key(key, KLEN);
    for (User u : readUsers()) {
      if (u.id.equals(id)) {
        return u.key.equals(k) ? u.level : "";
      }
    }
    return "";
  }

  static class Session { // ---------------------------------------------------
    String id;
    String key;
    String user;
    String connectionId;
    long expire;
    long lapse;

    Session (
      String id, String key, String user, String connectionId,
      long expire, long lapse
    ) {
      this.id = id;
      this.key = key;
      this.user = user;
      this.connectionId = connectionId;
      this.expire = expire;
      this.lapse = lapse;
    }

    Js toJs() {
      return Js.write(new ArrayList<Js>() {{
        add(Js.write(id));
        add(Js.write(key));
        add(Js.write(user));
        add(Js.write(connectionId));
        add(Js.write(expire));
        add(Js.write(lapse));
      }});
    }

    static Session ofJs (Js js) {
      ArrayList<Js> a = js.rArray();
      return new Session(
        a.get(0).rString(),
        a.get(1).rString(),
        a.get(2).rString(),
        a.get(3).rString(),
        (long)a.get(4).rDouble(),
        (long)a.get(5).rDouble()
      );
    }
  }

  static String sessionsPath () {
    return Std.fpath(Std.home(), SESSIONS_DB);
  }

  static void writeSessions(List<Session> ss) {
    Js js = Js.write(
      ss.stream().map(s -> s.toJs()).collect(Collectors.toList())
    );
    write(sessionsPath(), js.toString());
  }

  static List<Session> readSessions () {
    return new Js(read(sessionsPath())).rArray()
      .stream()
      .map(ujs -> Session.ofJs(ujs))
      .collect(Collectors.toList())
    ;
  }

  static void putSession(Session s) {
    List<Session> ss = readSessions();
    ss.add(s);
    writeSessions(ss);
  }
}
