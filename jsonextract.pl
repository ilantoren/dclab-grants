use strict;
use warnings;
use utf8;
use File::Slurp;
use JSON::XS;


open( my $fh, "<:encoding(utf8)", "test.json");
my $str = read_file( $fh );

my %data = decode_json  $str ;

print %data{'message'};
