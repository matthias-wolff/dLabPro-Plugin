/*
 * Original class in: org.apache.directory.ldapstudio.browser.ui.jobs
 * 
 * Copyright JSourcery.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tudresden.ias.eclipse.dlabpro.utils;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

class TimeTriggeredProgressMonitorDialog extends ProgressMonitorDialog
{

  private int              longOperationTime;
  private long             triggerTime  = -1;
  private boolean          dialogOpened = false;
  private IProgressMonitor wrapperedMonitor;

  public TimeTriggeredProgressMonitorDialog(Shell parent, int longOperationTime)
  {
    super(parent);
    setOpenOnRun(false);
    this.longOperationTime = longOperationTime;
  }

  public void createWrapperedMonitor()
  {
    wrapperedMonitor = new IProgressMonitor()
    {

      IProgressMonitor superMonitor = TimeTriggeredProgressMonitorDialog.super
                                        .getProgressMonitor();

      public void beginTask(String name, int totalWork)
      {
        superMonitor.beginTask(name,totalWork);
        checkTicking();
      }

      private void checkTicking()
      {
        if (triggerTime < 0) triggerTime = System.currentTimeMillis()
            + longOperationTime;
        if (!dialogOpened && System.currentTimeMillis() > triggerTime)
        {
          if (PlatformUI.getWorkbench().getDisplay().getActiveShell() == getParentShell())
          {
            open();
            dialogOpened = true;
          }
        }
      }

      public void done()
      {
        superMonitor.done();
        checkTicking();
      }

      public void internalWorked(double work)
      {
        superMonitor.internalWorked(work);
        checkTicking();
      }

      public boolean isCanceled()
      {
        return superMonitor.isCanceled();
      }

      public void setCanceled(boolean value)
      {
        superMonitor.setCanceled(value);
      }

      public void setTaskName(String name)
      {
        superMonitor.setTaskName(name);
        checkTicking();
      }

      public void subTask(String name)
      {
        superMonitor.subTask(name);
        checkTicking();
      }

      public void worked(int work)
      {
        superMonitor.worked(work);
        checkTicking();
      }
    };
  }

  public IProgressMonitor getProgressMonitor()
  {
    if (wrapperedMonitor == null) createWrapperedMonitor();
    return wrapperedMonitor;
  }

  public void run(final boolean fork, final boolean cancelable,
      final IRunnableWithProgress runnable) throws InvocationTargetException,
      InterruptedException
  {
    final InvocationTargetException[] invokes = new InvocationTargetException[1];
    final InterruptedException[] interrupt = new InterruptedException[1];
    Runnable dialogWaitRunnable = new Runnable()
    {

      public void run()
      {
        try
        {
          TimeTriggeredProgressMonitorDialog.super
              .run(fork,cancelable,runnable);
        }
        catch (InvocationTargetException e)
        {
          invokes[0] = e;
        }
        catch (InterruptedException e)
        {
          interrupt[0] = e;
        }
      }
    };
    final Display display = PlatformUI.getWorkbench().getDisplay();
    if (display == null) return;
    BusyIndicator.showWhile(display,dialogWaitRunnable);
    if (invokes[0] != null) { throw invokes[0]; }
    if (interrupt[0] != null) { throw interrupt[0]; }
  }
}
