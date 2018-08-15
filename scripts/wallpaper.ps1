#Get arguments
$path=$args[0]
$outputfile=$args[1]
$archive=$args[2]
$proxyhost=$args[3]
$proxyport=$args[4]

#Log file
Start-Transcript -Append "$outputfile"

#Arguments
Write-Output "arguments "
Write-Output "Path: " $path
Write-Output "Archive: " $archive
Write-Output "Proxy host: " $proxyhost
Write-Output "Proxy port: " $proxyport

#Execute Java program
Write-Output "execute java program"
java -jar "$path\nationalgeographic.jar" $path $archive $proxyhost $proxyport

#Update registry value for the correct wallpaper
Write-Output "Update registry in the correct wallpaper"
$WallpaperPath = "$path\photooftheday.jpg"
Set-ItemProperty -Path "HKCU:\Control Panel\Desktop\" -Name "Wallpaper" -Value $WallpaperPath

#Function to set the wallpaper and refresh it
Write-Output "Function to set the wallpaper and refresh it"
Add-Type @"
using System;
using System.Runtime.InteropServices;
using Microsoft.Win32;
namespace Wallpaper
{
   public enum Style : int
   {
       Tile, Center, Stretch, Fit, Fill, NoChange
   }
   public class Setter {
      public const int SetDesktopWallpaper = 20;
      public const int UpdateIniFile = 0x01;
      public const int SendWinIniChange = 0x02;
      [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
      private static extern int SystemParametersInfo (int uAction, int uParam, string lpvParam, int fuWinIni);
      public static void SetWallpaper ( string path, Wallpaper.Style style ) {
         SystemParametersInfo( SetDesktopWallpaper, 0, path, UpdateIniFile | SendWinIniChange );
         RegistryKey key = Registry.CurrentUser.OpenSubKey("Control Panel\\Desktop", true);
         switch( style )
         {
            case Style.Stretch :
               key.SetValue(@"WallpaperStyle", "2") ; 
               key.SetValue(@"TileWallpaper", "0") ;
               break;
            case Style.Center :
               key.SetValue(@"WallpaperStyle", "0") ; 
               key.SetValue(@"TileWallpaper", "0") ; 
               break;
            case Style.Tile :
               key.SetValue(@"WallpaperStyle", "0") ;  
               key.SetValue(@"TileWallpaper", "1") ;
               break;
			case Style.Fit :
               key.SetValue(@"WallpaperStyle", "6") ; 
               key.SetValue(@"TileWallpaper", "0") ;
               break;
			case Style.Fill :
               key.SetValue(@"WallpaperStyle", "10") ; 
               key.SetValue(@"TileWallpaper", "0") ;
               break;
            case Style.NoChange :
               break;
         }
         key.Close();
      }
   }
}
"@

#Set Wallpaper and stretch!
Write-Output "Set Wallpaper and stretch!"
[Wallpaper.Setter]::SetWallpaper( $WallpaperPath, 4 )