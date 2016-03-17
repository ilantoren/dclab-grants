use strict;
use warnings;
use utf8;
use File::Slurp;


print "$ARGV[0]\n";
open (my $dh, "<:encoding(utf-8)", $ARGV[0]);
my @lines = read_file($dh);
foreach my $ln ( @lines ) {
 my @axr = $ln =~ /<ce:grant-sponsor[^>]+>([^<]+)<\/ce:grant-sponsor>/;
 foreach my $fld ( @axr ) {
    print "$fld\n";
   }
}

