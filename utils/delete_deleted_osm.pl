#!/usr/bin/perl
my $deleting;
while(<>) {
    $deleting = $1 if !$deleting && /<(\w+).+action='delete'/;
    print if !$deleting;
    undef $deleting if $deleting && /<\/$deleting/;
}
