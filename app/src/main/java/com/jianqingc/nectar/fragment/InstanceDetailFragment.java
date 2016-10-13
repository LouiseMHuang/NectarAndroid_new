package com.jianqingc.nectar.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jianqingc.nectar.R;
import com.jianqingc.nectar.controller.HttpRequestController;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstanceDetailFragment extends Fragment {
    View myView;
    String instanceId;
    String instanceName ;
    String zone ;
    String address ;
    String instanceStatus ;
    public InstanceDetailFragment() {
        // Required empty public constructor
    }
    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();

    }
    public void enable(Button btn){
        btn.setEnabled(true);
        btn.setVisibility(View.VISIBLE);
        btn.setBackgroundColor(Color.parseColor("#31319b"));
        if (btn.getText().equals("DELETE")){
            btn.setBackgroundColor(Color.parseColor("#990000"));
        }
        btn.setTextColor(Color.parseColor("#ffffff"));
    }
    public void disable(Button btn){
        btn.setEnabled(false);
        btn.setVisibility(View.VISIBLE);
        btn.setBackgroundColor(Color.parseColor("#e6e6e6"));
        btn.setTextColor(Color.parseColor("#8c8c8c"));

    }
    public void hide(Button btn){
        btn.setEnabled(false);
        btn.setVisibility(View.GONE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_instance_detail, container, false);
        Bundle bundle = getArguments();
        instanceId = bundle.getString("instanceId");
        final Button startBtn = (Button)myView.findViewById(R.id.startBtn);
        final Button stopBtn = (Button)myView.findViewById(R.id.stopBtn);
        final Button pauseBtn = (Button)myView.findViewById(R.id.pauseBtn);
        final Button unpauseBtn = (Button)myView.findViewById(R.id.unpauseBtn);
        final Button suspendBtn = (Button)myView.findViewById(R.id.suspendBtn);
        final Button resumeBtn = (Button)myView.findViewById(R.id.resumeBtn);
        final Button deleteBtn = (Button)myView.findViewById(R.id.deleteBtn);
        final Button rebootBtn = (Button)myView.findViewById(R.id.rebootBtn);
        final Button snapshotBtn = (Button)myView.findViewById(R.id.snapshotBtn);
        final java.util.Timer timer = new java.util.Timer(true);
        HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                setView(result);
            }
        }, getActivity().getApplicationContext(), instanceId);
// set button onclick
        //set dialog which shows the spinner
        final Dialog mOverlayDialog = new Dialog(getActivity(), android.R.style.Theme_Panel); //display an invisible overlay dialog to prevent user interaction and pressing back
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.setContentView(R.layout.loading_dialog);
//Set refresh/back button.
        FloatingActionButton fabRight = (FloatingActionButton) getActivity().findViewById(R.id.fabRight);
        fabRight.setVisibility(View.VISIBLE);
        fabRight.setEnabled(true);
        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOverlayDialog.show();
                HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        setView(result);
                        mOverlayDialog.dismiss();
                    }
                }, getActivity().getApplicationContext(), instanceId);
                Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG).show();
            }
        });
        FloatingActionButton fabLeft = (FloatingActionButton) getActivity().findViewById(R.id.fabLeft);
        fabLeft.setVisibility(View.VISIBLE);
        fabLeft.setEnabled(true);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                InstanceFragment instanceFragment = new InstanceFragment();
                manager.beginTransaction().replace(R.id.relativelayout_for_fragment, instanceFragment, instanceFragment.getTag()).commit();
            }
        });
        //set pause button
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Pause this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOverlayDialog.show();
                        HttpRequestController.getInstance(getActivity().getApplicationContext()).pause(new HttpRequestController.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                if(result.equals("success")) {
                                    TimerTask task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                    Toast.makeText(getActivity().getApplicationContext(), "Pause Instance Succeed", Toast.LENGTH_SHORT).show();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    };
                                    timer.schedule(task, 7000);
                                } else{
                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                        @Override
                                        public void onSuccess(String result) {
                                            setView(result);
                                            mOverlayDialog.dismiss();
                                        }
                                    }, getActivity().getApplicationContext(), instanceId);
                                }
                            }
                        }, instanceId);
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set unpause button
        unpauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Unpause this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).unpause(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Unpause Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set stop button
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Stop this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).stop(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Stop Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set start button
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Start this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).start(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Start Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //set suspend button
        suspendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Suspend this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).suspend(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Suspend Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set resume button
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Resume this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).resume(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Resume Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set reboot button
        rebootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Reboot this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).reboot(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Reboot Instance Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete this instance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).delete(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Delete Instance Succeed", Toast.LENGTH_SHORT).show();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    mOverlayDialog.dismiss();
                                                    FragmentManager manager = getFragmentManager();
                                                    InstanceFragment instanceFragment = new InstanceFragment();
                                                    manager.beginTransaction().replace(R.id.relativelayout_for_fragment, instanceFragment, instanceFragment.getTag()).commit();
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        // set snapshot button
        snapshotBtn.setOnClickListener(new View.OnClickListener() {
            EditText input = new EditText(getActivity());
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Please enter the name of this snapshot:").setView(input)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOverlayDialog.show();
                                String snapshotName = input.getText().toString();
                                HttpRequestController.getInstance(getActivity().getApplicationContext()).snapshot(new HttpRequestController.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("success")) {
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            setView(result);
                                                            mOverlayDialog.dismiss();
                                                            Toast.makeText(getActivity().getApplicationContext(), "Snapshot Succeed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, getActivity().getApplicationContext(), instanceId);
                                                }
                                            };
                                            timer.schedule(task, 7000);
                                        } else{
                                            HttpRequestController.getInstance(getActivity().getApplicationContext()).listSingleInstance(new HttpRequestController.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    setView(result);
                                                    mOverlayDialog.dismiss();
                                                }
                                            }, getActivity().getApplicationContext(), instanceId);
                                        }
                                    }
                                }, instanceId,snapshotName);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });


        return myView;
    }

    @Override
    public void onPause() {
        // Remove refresh and back buttons when this fragment is hiden.
        super.onPause();
        FloatingActionButton fabRight = (FloatingActionButton) getActivity().findViewById(R.id.fabRight);
        FloatingActionButton fabLeft = (FloatingActionButton) getActivity().findViewById(R.id.fabLeft);
        fabRight.setVisibility(View.GONE);
        fabRight.setEnabled(false);
        fabLeft.setVisibility(View.GONE);
        fabLeft.setEnabled(false);
    }


    public void setView(String result){
        // set the textviews and buttons according to the instance status.
        try {
            JSONObject JSONResult = new JSONObject(result);
            instanceId = JSONResult.getString("id");
            instanceName = JSONResult.getString("name");
            zone = JSONResult.getString("zone");
            address = JSONResult.getString("address");
            instanceStatus = JSONResult.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView instanceIdTV = (TextView)myView.findViewById(R.id.instanceIdTV);
        TextView instanceNameTV= (TextView)myView.findViewById(R.id.instanceNameTV);
        TextView instanceZoneTV = (TextView)myView.findViewById(R.id.instanceZoneTV);
        TextView instanceIPAddressTV = (TextView)myView.findViewById(R.id.instanceIPAddressTV);
        TextView instanceStatusTV = (TextView)myView.findViewById(R.id.instanceStatusTV);
        Button startBtn = (Button)myView.findViewById(R.id.startBtn);
        Button stopBtn = (Button)myView.findViewById(R.id.stopBtn);
        Button pauseBtn = (Button)myView.findViewById(R.id.pauseBtn);
        Button unpauseBtn = (Button)myView.findViewById(R.id.unpauseBtn);
        Button suspendBtn = (Button)myView.findViewById(R.id.suspendBtn);
        Button resumeBtn = (Button)myView.findViewById(R.id.resumeBtn);
        Button deleteBtn = (Button)myView.findViewById(R.id.deleteBtn);
        Button rebootBtn = (Button)myView.findViewById(R.id.rebootBtn);
        Button snapshotBtn = (Button)myView.findViewById(R.id.snapshotBtn);
        instanceIdTV.setText(instanceId);
        instanceNameTV.setText(instanceName);
        instanceZoneTV.setText(zone);
        instanceIPAddressTV.setText(address);
        instanceStatusTV.setText(instanceStatus);

        // set status text color
        switch (instanceStatus) {
            case "ACTIVE":
                instanceStatusTV.setTextColor(Color.parseColor("#2eb82e"));//green
                break;
            case "DELETED":
            case "ERROR":
            case "PAUSED":
            case "SHUTOFF":
            case "SUSPENDED":
                instanceStatusTV.setTextColor(Color.parseColor("#ff0000"));//red
                break;
            default:
                instanceStatusTV.setTextColor(Color.parseColor("#e9a92a"));//orange
                break;
        }

        //set button visibility
        switch (instanceStatus) {
            case "ACTIVE":
                enable(pauseBtn);
                enable(suspendBtn);
                enable(stopBtn);
                enable(rebootBtn);
                enable(snapshotBtn);
                enable(deleteBtn);
                hide(unpauseBtn);
                hide(resumeBtn);
                hide(startBtn);
                break;
            case "SUSPENDED":
                disable(pauseBtn);
                hide(unpauseBtn);
                enable(resumeBtn);
                hide(suspendBtn);
                disable(stopBtn);
                hide(startBtn);
                disable(rebootBtn);
                enable(snapshotBtn);
                enable(deleteBtn);
                break;
            case "PAUSED":
                enable(unpauseBtn);
                hide(pauseBtn);
                disable(suspendBtn);
                hide(resumeBtn);
                disable(stopBtn);
                hide(startBtn);
                disable(rebootBtn);
                enable(snapshotBtn);
                enable(deleteBtn);
                break;
            case "SHUTOFF":
                disable(pauseBtn);
                hide(unpauseBtn);
                disable(suspendBtn);
                hide(resumeBtn);
                hide(stopBtn);
                enable(startBtn);
                enable(rebootBtn);
                enable(snapshotBtn);
                enable(deleteBtn);
                break;
            default:
                disable(pauseBtn);
                hide(unpauseBtn);
                disable(suspendBtn);
                hide(resumeBtn);
                disable(stopBtn);
                hide(startBtn);
                disable(rebootBtn);
                disable(snapshotBtn);
                disable(deleteBtn);
                break;
        }

    }


}
