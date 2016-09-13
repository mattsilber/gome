### Use

Clone repo.

Rename to whatever.

Replace all instances of the package 'com.guardanis.gome' with your desired package name:

    grep -rl 'com.guardanis.gome' ./ | xargs sed -i 's/com.guardanis.gome/some.new.package/g'

Then rename the directories for 'com/guardanis.gome' to match your package:

    mv src/main/java/com/guardanis.gome/* src/main/java/some/new/package/

Import with AS.

Boom. Done.
