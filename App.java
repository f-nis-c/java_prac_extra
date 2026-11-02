//exp 6 extra
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class App {
    static List<String> sub = new ArrayList<>(Arrays.asList("Java"));
    static List<String> stu = new ArrayList<>(Arrays.asList("Kanak", "Falak"));
    static Map<String, Map<String, Map<String, Boolean>>> att = new HashMap<>();
    public static void main(String[] args) throws Exception {
        int port = 8081;
        HttpServer svr = HttpServer.create(new InetSocketAddress(port), 0);
        svr.createContext("/java/attendance", App::handle);
        svr.start();
        System.out.println("Running on: http://localhost:" + port + "/java/attendance");
    }
    static void handle(HttpExchange ex) throws IOException {
        String uri = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();
        if ("GET".equals(method)) {
            if (uri.equals("/java/attendance") || uri.equals("/java/attendance/")) {
                send(ex, 200, "text/html; charset=UTF-8", html());
                return;
            } else if (uri.endsWith("/api/dat")) {
                send(ex, 200, "application/json", json());
                return;
            }
        } else if ("POST".equals(method)) {
            Map<String, String> p = parse(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String v = p.getOrDefault("val", "").trim();
            if (uri.endsWith("/sub") && !v.isEmpty() && !sub.contains(v)) {
                sub.add(v);
            } else if (uri.endsWith("/stu/add") && !v.isEmpty() && !stu.contains(v)) {
                stu.add(v);
            } else if (uri.endsWith("/stu/del") && !v.isEmpty()) {
                stu.remove(v);
                for (Map<String, Map<String, Boolean>> d : att.values()) {
                    for (Map<String, Boolean> s : d.values()) {
                        s.remove(v);
                    }
                }
            } else if (uri.endsWith("/mrk")) {
                String d = p.get("dte"), s = p.get("sub"), st = p.get("stu");
                boolean status = Boolean.parseBoolean(p.get("val"));
                if (d != null && s != null && st != null) {
                    if (!att.containsKey(d)) att.put(d, new HashMap<>());
                    if (!att.get(d).containsKey(s)) att.get(d).put(s, new HashMap<>());

                    Map<String, Boolean> classSession = att.get(d).get(s);

                    // When a class happens, ensure all other enrolled students default to Absent (false)
                    if (status) {
                        for (String existingStudent : stu) {
                            classSession.putIfAbsent(existingStudent, false);
                        }
                    }
                    classSession.put(st, status);
                }
            }
            send(ex, 200, "application/json", "{\"ok\":true}");
            return;
        }
        send(ex, 404, "text/plain", "Not Found");
    }
    static void send(HttpExchange ex, int code, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
    static Map<String, String> parse(String q) {
        Map<String, String> map = new HashMap<>();
        if (q == null || q.isEmpty()) return map;
        for (String pair : q.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name()),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()));
                } catch (Exception ignored) {}
            }
        }
        return map;
    }
    static String json() {
        StringBuilder b = new StringBuilder("{");
        b.append("\"sub\":[");
        for (int i = 0; i < sub.size(); i++) {
            b.append("\"").append(sub.get(i)).append("\"").append(i < sub.size() - 1 ? "," : "");
        }
        b.append("],\"stu\":[");
        for (int i = 0; i < stu.size(); i++) {
            b.append("\"").append(stu.get(i)).append("\"").append(i < stu.size() - 1 ? "," : "");
        }
        b.append("],\"att\":{");
        int i = 0;
        for (Map.Entry<String, Map<String, Map<String, Boolean>>> d : att.entrySet()) {
            b.append("\"").append(d.getKey()).append("\":{");
            int j = 0;
            for (Map.Entry<String, Map<String, Boolean>> s : d.getValue().entrySet()) {
                b.append("\"").append(s.getKey()).append("\":{");
                int k = 0;
                for (Map.Entry<String, Boolean> t : s.getValue().entrySet()) {
                    b.append("\"").append(t.getKey()).append("\":").append(t.getValue());
                    if (++k < s.getValue().size()) b.append(",");
                }
                b.append("}").append(++j < d.getValue().size() ? "," : "");
            }
            b.append("}").append(++i < att.size() ? "," : "");
        }
        return b.append("}}").toString();
    }
    static String html() {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>Attendance Tracker</title><style>"
            + ":root{--bg:#0b0f19;--card:#111827;--border:#1e293b;--text:#e2e8f0;--muted:#94a3b8;--accent:#38bdf8;--accent-hover:#0284c7;--danger:#f87171;--danger-bg:#450a0a;--ok:#34d399;--ok-bg:#064e3b}"
            + "*{box-sizing:border-box;margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}"
            + "body{background:var(--bg);color:var(--text);padding:24px 16px}"
            + ".cnt{max-width:880px;margin:0 auto;display:flex;flex-direction:column;gap:18px}"
            + ".crd{background:var(--card);border-radius:12px;padding:20px;border:1px solid var(--border)}"
            + "h3{font-size:16px;font-weight:600;color:#f1f5f9;margin-bottom:12px;display:flex;align-items:center;gap:8px}"
            + ".row{display:flex;gap:10px;align-items:center;flex-wrap:wrap}"
            + "label{font-size:13px;color:var(--muted)}"
            + "input,select,button{padding:9px 12px;border-radius:8px;border:1px solid var(--border);background:#0f172a;color:var(--text);font-size:14px;outline:none}"
            + "input:focus,select:focus{border-color:var(--accent)}"
            + "button{background:var(--accent);color:#031320;border:none;cursor:pointer;font-weight:600;transition:0.15s}"
            + "button:hover{background:var(--accent-hover);color:#fff}"
            + ".del{background:var(--danger-bg);color:var(--danger);border:1px solid rgba(248,113,113,0.2);padding:4px 10px;margin-left:auto}"
            + ".del:hover{background:var(--danger);color:#fff}"
            + "ul{list-style:none;margin-top:12px;display:flex;flex-direction:column;gap:8px}"
            + "li{display:flex;align-items:center;background:#0f172a;padding:8px 12px;border-radius:8px;border:1px solid var(--border);font-size:14px}"
            + "table{width:100%;border-collapse:collapse;margin-top:12px}"
            + "th,td{padding:10px 12px;border-bottom:1px solid var(--border);text-align:left;font-size:14px}"
            + "th{background:#0f172a;color:var(--muted);font-weight:500}"
            + ".badge{padding:3px 8px;border-radius:6px;font-size:12px;font-weight:600;display:inline-block}"
            + ".badge.ok{background:var(--ok-bg);color:var(--ok)}"
            + ".badge.no{background:var(--danger-bg);color:var(--danger)}"
            + ".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:16px}"
            + ".sum-card{background:#0f172a;border:1px solid var(--border);border-radius:8px;padding:12px 14px;display:flex;justify-content:space-between;align-items:center}"
            + "</style></head><body><div class='cnt'>"

            + "<div class='crd'><h3>Overall Attendance (Total Classes Conducted)</h3>"
            + "<div id='ovr' class='grid'></div></div>"

            + "<div class='crd'><h3>Mark Daily Attendance</h3>"
            + "<div class='row'><label>Date</label><input type='date' id='dte' onchange='ren()'>"
            + "<label>Subject</label><select id='sbj' onchange='ren()'></select></div>"
            + "<table><thead><tr><th>Student</th><th>Status</th></tr></thead><tbody id='tbl'></tbody></table></div>"

            + "<div class='row' style='align-items:stretch'>"
            + "<div class='crd' style='flex:1'><h3>Add Subject</h3><div class='row'><input id='asb' placeholder='Subject name' style='flex:1'><button onclick='asb()'>Add</button></div></div>"
            + "<div class='crd' style='flex:1'><h3>Add Student</h3><div class='row'><input id='ast' placeholder='Student name' style='flex:1'><button onclick='ast()'>Add</button></div></div>"
            + "</div>"

            + "<div class='crd'><h3>Active Students</h3><ul id='stl'></ul></div>"

            + "<div class='crd'><h3>History by Subject</h3>"
            + "<div class='row'><label>Student</label><select id='sst' onchange='rep()'></select>"
            + "<label>Subject</label><select id='rsb' onchange='rep()'></select></div>"
            + "<table><thead><tr><th>Date</th><th>Status</th></tr></thead><tbody id='rpt'></tbody></table></div>"

            + "</div><script>"
            + "let dat={sub:[],stu:[],att:{}};"
            + "document.getElementById('dte').value=new Date().toISOString().split('T')[0];"
            + "async function req(ep,body){await fetch('/java/attendance/api/'+ep,{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(body)});await load();}"
            + "async function load(){let res=await fetch('/java/attendance/api/dat');dat=await res.json();pop();ren();rep();ovr();}"
            + "async function asb(){let v=document.getElementById('asb').value.trim();if(!v)return;await req('sub',{val:v});document.getElementById('asb').value='';}"
            + "async function ast(){let v=document.getElementById('ast').value.trim();if(!v)return;await req('stu/add',{val:v});document.getElementById('ast').value='';}"
            + "function pop(){"
            + "  let fill=(id,arr)=>{let el=document.getElementById(id),cur=el.value;el.innerHTML=arr.map(x=>`<option value='${x}'>${x}</option>`).join('');if(cur&&arr.includes(cur))el.value=cur;};"
            + "  fill('sbj',dat.sub);fill('rsb',dat.sub);fill('sst',dat.stu);"
            + "  document.getElementById('stl').innerHTML=dat.stu.map(x=>`<li><span>${x}</span><button class='del' onclick=\"req('stu/del',{val:'${x}'})\">Delete</button></li>`).join('')||'<li style=\"color:var(--muted)\">No students added</li>';"
            + "}"
            + "function ren(){"
            + "  let d=document.getElementById('dte').value,s=document.getElementById('sbj').value,b=document.getElementById('tbl');"
            + "  if(!s||!d){b.innerHTML='<tr><td colspan=\"2\" style=\"color:var(--muted)\">Select date and subject</td></tr>';return;}"
            + "  b.innerHTML=dat.stu.map(x=>{"
            + "    let hasRecord=dat.att[d]&&dat.att[d][s]&&dat.att[d][s][x]!==undefined;"
            + "    let st=hasRecord&&dat.att[d][s][x]===true;"
            + "    let statusText=hasRecord?(st?'Present':'Absent'):'Unmarked';"
            + "    let badgeClass=hasRecord?(st?'ok':'no'):'';"
            + "    return `<tr><td>${x}</td><td><label style='display:flex;align-items:center;gap:8px;cursor:pointer'><input type='checkbox' ${st?'checked':''} onchange=\"req('mrk',{dte:'${d}',sub:'${s}',stu:'${x}',val:this.checked})\"><span class='badge ${badgeClass}'>${statusText}</span></label></td></tr>`;"
            + "  }).join('');"
            + "}"
            + "function rep(){"
            + "  let u=document.getElementById('sst').value,s=document.getElementById('rsb').value,b=document.getElementById('rpt');"
            + "  if(!u||!s){b.innerHTML='';return;}"
            + "  let dts=Object.keys(dat.att).filter(d=>dat.att[d][s]&&dat.att[d][s][u]!==undefined).sort().reverse();"
            + "  b.innerHTML=dts.map(d=>{"
            + "    let st=dat.att[d][s][u]===true;"
            + "    return `<tr><td>${d}</td><td><span class='badge ${st?'ok':'no'}'>${st?'Present':'Absent'}</span></td></tr>`;"
            + "  }).join('')||'<tr><td colspan=\"2\" style=\"color:var(--muted)\">No records found</td></tr>';"
            + "}"
            + "function ovr(){"
            + "  let o=document.getElementById('ovr');"
            + "  if(!dat.stu.length){o.innerHTML='<div style=\"color:var(--muted)\">No students registered yet.</div>';return;}"
            + "  let totalHeldClasses=0;"
            + "  for(let d in dat.att){"
            + "    for(let s in dat.att[d]){"
            + "      let wasHeld=Object.values(dat.att[d][s]).some(v=>v===true);"
            + "      if(wasHeld) totalHeldClasses++;"
            + "    }"
            + "  }"
            + "  o.innerHTML=dat.stu.map(st=>{"
            + "    let attended=0;"
            + "    for(let d in dat.att){"
            + "      for(let s in dat.att[d]){"
            + "        if(dat.att[d][s][st]===true) attended++;"
            + "      }"
            + "    }"
            + "    let pct=totalHeldClasses>0?Math.round((attended/totalHeldClasses)*100):0;"
            + "    return `<div class='sum-card'><div><div style='font-weight:600'>${st}</div><div style='font-size:12px;color:var(--muted)'>Attended ${attended} of ${totalHeldClasses} classes held</div></div><span class='badge ${pct>=75?'ok':'no'}'>${pct}%</span></div>`;"
            + "  }).join('');"
            + "}"
            + "load();</script></body></html>";
    }
}
