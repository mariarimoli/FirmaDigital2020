; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "TOKEN SIGNING"
#define MyAppVersion "1.0.0.6"
#define MyAppPublisher "Firebreath Solution, Inc."
#define MyAppURL "http://firebreath.com//"
#define MyAppExeName  "TokenSigning.exe"   
#define AppMgrName "TOKEN SIGNING MGR"       
#define AppMgrExeName "TokenSigningMgr.exe"           

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{17A88AA2-F101-4CDD-820B-3271562A91EB}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
VersionInfoVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
OutputBaseFilename={#MyAppName}_Setup_{#MyAppVersion}
UninstallDisplayName={#MyAppName}
Compression=lzma
SolidCompression=yes
OutputDir = Setup Files
CloseApplications=no
PrivilegesRequired=none            


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Code]
procedure TaskKillAll;
var
  ResultCode: Integer;
begin
    Exec(ExpandConstant('taskkill.exe'), '/f /im ' + '"TokenSigning.exe"', '', SW_HIDE,
      ewWaitUntilTerminated, ResultCode);
end;
procedure TaskKill(FileName: String);
var
  ResultCode: Integer;
begin
    Exec(ExpandConstant('taskkill.exe'), '/f /im ' + '"' + FileName + '"', '', SW_HIDE,
     ewWaitUntilTerminated, ResultCode);
end;


//Remove setting
procedure CurUninstallStepChanged (CurUninstallStep: TUninstallStep);
 var
     mres : integer;
 begin
    case CurUninstallStep of                   
      usPostUninstall:
        begin 
            DelTree(ExpandConstant('{userappdata}\TokenSigning Config'), True, True, True);
       end;
   end;
end;

function CheckUserLogged(): Boolean;
begin
  if IsAdminLoggedOn then
  begin
    Result := False;
  end
    else
  begin
    Result := True;
  end;
end;

function GetDefaultDirName(Param: string): string;
begin
  if IsAdminLoggedOn then
  begin
    Result := ExpandConstant('{pf}\My Program');
  end
    else
  begin
    Result := ExpandConstant('{userappdata}\My Program');
  end;
end;

var
  OptionPage: TInputOptionWizardPage;


[Files] 
Source: "TokenSigning Config\*"; DestDir: "{userappdata}\TokenSigning Config"; Flags: ignoreversion recursesubdirs; BeforeInstall: TaskKillAll                                                   
Source: "Plugin\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; 

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"   

[Registry] 
Root: HKLM; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Version";ValueData: {#MyAppVersion};    Check: IsAdminLoggedOn;
Root: HKLM; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Path";ValueData: {app}\{#MyAppExeName}; Check: IsAdminLoggedOn;
Root: HKLM; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Folder";ValueData: {app};               Check: IsAdminLoggedOn;
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; Flags: uninsdeletekey; ValueType: string; ValueName: "{#MyAppName}"; ValueData: "{app}\{#MyAppExeName}"; Check: IsAdminLoggedOn;
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; Flags: uninsdeletekey; ValueType: string; ValueName: "{#AppMgrName}"; ValueData: "{app}\{#AppMgrExeName}"; Check: IsAdminLoggedOn;

Root: HKCU; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Version";ValueData: {#MyAppVersion};                         Check: CheckUserLogged;
Root: HKCU; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Path";ValueData: {app}\{#MyAppExeName};                      Check: CheckUserLogged;
Root: HKCU; Subkey: "SOFTWARE\{#MyAppName}"; Flags: uninsdeletekey; ValueType: string; ValueName: "Folder";ValueData: {app};                                    Check: CheckUserLogged;
Root: HKCU; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; Flags: uninsdeletekey; ValueType: string; ValueName: "{#MyAppName}"; ValueData: "{app}\{#MyAppExeName}"; Check: CheckUserLogged
Root: HKCU; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; Flags: uninsdeletekey; ValueType: string; ValueName: "{#AppMgrName}"; ValueData: "{app}\{#AppMgrExeName}"; Check: CheckUserLogged


[Run]
Filename: "{app}\TokenSigningSetup.exe"; Parameters: /passive /norestart
Filename: "{app}\{#MyAppExeName}"; Flags: nowait skipifsilent runasoriginaluser

[UninstallRun]
Filename: "{cmd}"; Parameters: "/C ""taskkill /im ""{#MyAppExeName}"" /f /t"
