#!/usr/bin/perl
use strict;

if( $#ARGV == 1 && $ARGV[1] =~ /#[a-fA-F0-9]{6}/ ) {
  if( open F, '<'.$ARGV[0] ) {
    while(<F>) {
      s/#555555/$ARGV[1]/g;
      print;
    }
    close F;
  }
  exit;
}

my %lines;
open LINES, '<lines.csv' or die 'Cannot open lines.csv';
while(<LINES>) {
  $lines{$1} = $2 if /(\d+);[^;]+;(#[a-fA-F0-9]{6})/;
}
close LINES;

mkdir 'icons' or die "Cannot create icons directory" if !-e 'icons';

foreach my $line (keys %lines) {
  my $color = $lines{$line};
  `perl $0 ../../art/icons/$_.svg $color | rsvg-convert -o icons/$line$_.png` for 0..9;
}