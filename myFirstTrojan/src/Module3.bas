Attribute VB_Name = "Module3"
'En este módulo hay las definiciones y estructuras necesarias para recaptar
' información del ordenador afectado

'Extraído de http://www.allapi.net/apilist/GetVersionEx.shtml

'Para obtener la función de Windows
Public Declare Function GetVersionExA Lib "kernel32" (lpVersionInformation As OSVERSIONINFO) As Long

Public Type OSVERSIONINFO
    dwOSVersionInfoSize As Long
    dwMajorVersion As Long
    dwMinorVersion As Long
    dwBuildNumber As Long
    dwPlatformId As Long
    szCSDVersion As String * 128
End Type

Public Enum Enum_OperatingPlatform
  Platform_Windows_32 = 0
  Platform_Windows_95_98_ME = 1
  Platform_Windows_NT_2K_XP = 2
End Enum

Public Enum Enum_OperatingSystem
  System_Windows_32 = 0
  System_Windows_95 = 1
  System_Windows_98 = 2
  System_Windows_ME = 3
  System_Windows_NT = 4
  System_Windows_2K = 5
  System_Windows_XP = 6
End Enum

'Para obtener el nombre de la computadora
Declare Function GetComputerName Lib "kernel32.dll" Alias "GetComputerNameA" (ByVal lpbuffer As String, nsize As Long) As Long
Declare Function DllGetVersion Lib "Shlwapi.dll" (dwVersion As DllVersionInfo) As Long

'Para obtener la versión del IE
Type DllVersionInfo
    cbSize As Long
    dwMajorVersion As Long
    dwMinorVersion As Long
    dwBuildNumber As Long
    dwPlatformId As Long
End Type

'Para acceder al registro
Public Const HKEY_CLASSES_ROOT = &H80000000

Declare Function RegQueryValue Lib "advapi32.dll" Alias "RegQueryValueA" _
    (ByVal hKey As Long, ByVal lpSubKey As String, ByVal lpValue As String, _
     lpcbValue As Long) As Long




 
