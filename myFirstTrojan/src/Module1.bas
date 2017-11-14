Attribute VB_Name = "Module1"
'En este módulo hay las definiciones y estructuras necesarias para recaptar
' una lista de los ficheros del ordenador afectado.

'Para escanear los ficheros de los discos...
Declare Function GetLogicalDrives Lib "kernel32" () As Long

Declare Function FindFirstFile Lib "kernel32" Alias "FindFirstFileA" _
                        (ByVal lpFileName As String, lpFindFileData As WIN32_FIND_DATA) As Long
                        
' Rtns True (non zero) on succes, False on failure
Declare Function FindNextFile Lib "kernel32" Alias "FindNextFileA" _
                        (ByVal hFindFile As Long, lpFindFileData As WIN32_FIND_DATA) As Long
                        
' Rtns True (non zero) on succes, False on failure
Declare Function FindClose Lib "kernel32" (ByVal hFindFile As Long) As Long

'FindFirstFile failure rtn value
Public Const INVALID_HANDLE_VALUE = -1
                        
Public Const MaxLFNPath = 260
Type FILETIME
        dwLowDateTime As Long
        dwHighDateTime As Long
End Type
Type WIN32_FIND_DATA
        dwFileAttributes As Long
        ftCreationTime As FILETIME
        ftLastAccessTime As FILETIME
        ftLastWriteTime As FILETIME
        nFileSizeHigh As Long
        nFileSizeLow As Long
        dwReserved0 As Long
        dwReserved1 As Long
        cFileName As String * MaxLFNPath
        cShortFileName As String * 14
End Type

  
  

